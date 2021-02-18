package net.ntworld.sentryIntegrationIdea.editor

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.editor.gutter.GutterState
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import kotlin.concurrent.thread

class EditorManagerImpl(
    private val projectServiceProvider: ProjectServiceProvider
) : EditorManager {
    private val myFileEditorManagerEx = FileEditorManagerEx.getInstanceEx(projectServiceProvider.project)
    private val myControllerMap = mutableMapOf<TextEditor, EditorController>()

    override fun open(frame: Storage.Frame) {
        thread {
            FindVirtualFileRunnable(projectServiceProvider, frame, this::handleFindVirtualFile).start()
        }
    }

    internal fun handleFindVirtualFile(frame: Storage.Frame, virtualFile: VirtualFile) {
        ApplicationManager.getApplication().invokeLater {
            val editors = myFileEditorManagerEx.openFile(virtualFile, false)
            for (fileEditor in editors) {
                if (fileEditor is TextEditor) {
                    val controller = bindControllerToTextEditor(fileEditor)
                    if (null !== controller) {
                        controller.initializeLine(frame)
                        when (frame.source) {
                            Storage.FrameSource.MAIN_UI -> handleOpenFrameFromMainUI(controller, frame)
                            Storage.FrameSource.WORKER -> { }
                        }
                    }
                }
            }
        }
    }

    private fun handleOpenFrameFromMainUI(controller: EditorController, frame: Storage.Frame) {
        if (frame.index == frame.lastIndex) {
            controller.setGutterIconState(frame.visibleLine, GutterState.LAST_FRAME)
        }
        controller.hideAllFramesInWholeEditor()
        controller.displaySingleFrame(frame)
        controller.scrollToLine(frame.visibleLine)
    }

    private fun bindControllerToTextEditor(textEditor: TextEditor): EditorController? {
        if (myControllerMap.contains(textEditor)) {
            return myControllerMap[textEditor]
        }

        val editor = textEditor.editor as? EditorEx ?: return null
        val instance = EditorControllerImpl(
            projectServiceProvider,
            textEditor,
            editor
        )
        myControllerMap[textEditor] = instance
        return instance
    }

    class FindVirtualFileRunnable(
        private val projectServiceProvider: ProjectServiceProvider,
        private val frame: Storage.Frame,
        private val invoker: (Storage.Frame, VirtualFile) -> Unit
    ): Runnable {

        fun start() {
            ApplicationManager.getApplication().runReadAction(this)
        }

        override fun run() {
            val repositoryManager = projectServiceProvider.makeRepositoryManager(frame.linkedProject)
            val filePath = repositoryManager.findLocalFilePath(frame.linkedProject, frame)
            if (isFileContentMatchedWithFrameContext(filePath.virtualFile, frame)) {
                return invoker.invoke(frame, filePath.virtualFile!!)
            }

            val isSourceFile = repositoryManager.isSourceFile(frame.linkedProject, frame)
            if (!isSourceFile) {
                return
            }

            val vcsVirtualFile = repositoryManager.findVcsVirtualFile(frame.linkedProject, frame)
            if (null !== vcsVirtualFile) {
                return invoker.invoke(frame, vcsVirtualFile)
            }
        }

        private fun isFileContentMatchedWithFrameContext(virtualFile: VirtualFile?, frame: Storage.Frame): Boolean {
            if (null === virtualFile || virtualFile.isDirectory) {
                return false
            }

            if (!virtualFile.isValid || !virtualFile.exists()) {
                return false
            }

            val document = FileDocumentManager.getInstance().getDocument(virtualFile)
            if (null === document) {
                return false
            }
            for (item in frame.context) {
                val logicalLine = item.line - 1
                val startOffset = document.getLineStartOffset(logicalLine)
                val endOffset = document.getLineEndOffset(logicalLine)
                val text = document.getText(TextRange(startOffset, endOffset))
                if (text != item.content) {
                    return false
                }
            }

            return true
        }
    }

}