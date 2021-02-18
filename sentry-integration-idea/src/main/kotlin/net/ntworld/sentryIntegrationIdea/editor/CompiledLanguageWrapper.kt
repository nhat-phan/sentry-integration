package net.ntworld.sentryIntegrationIdea.editor

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider
import kotlin.concurrent.thread

class CompiledLanguageWrapper(
    private val projectServiceProvider: ProjectServiceProvider,
    private val editorManagerImpl: EditorManagerImpl
) : EditorManager {

    override fun open(frame: Storage.Frame) {
        thread {
            FindVirtualFileRunnable(projectServiceProvider, frame, editorManagerImpl::handleFindVirtualFile).start()
        }
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
            val isSourceFile = repositoryManager.isSourceFile(frame.linkedProject, frame)
            if (!isSourceFile) {
                val virtualFile = repositoryManager.findLocalVirtualFile(frame.linkedProject, frame)
                if (null !== virtualFile) {
                    return invoker.invoke(frame, virtualFile)
                } else {
                    return
                }
            }

            // TODO: find and matched by release version
            val vcsVirtualFile = repositoryManager.findVcsVirtualFile(frame.linkedProject, frame)
            if (null !== vcsVirtualFile) {
                return invoker.invoke(frame, vcsVirtualFile)
            }
        }
    }
}