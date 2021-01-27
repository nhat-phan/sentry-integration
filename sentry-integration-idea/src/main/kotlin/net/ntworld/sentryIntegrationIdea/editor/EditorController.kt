package net.ntworld.sentryIntegrationIdea.editor

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.TextEditor
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.editor.gutter.GutterState

/**
 * Purpose of EditorController is control all actions inside an editor
 */
interface EditorController : Disposable {
    val textEditor: TextEditor

    val editor: EditorEx

    fun initializeLine(frame: Storage.Frame)

    fun hideAllFramesInWholeEditor()

    fun scrollToLine(visibleLine: Int)

    fun displaySingleFrame(frame: Storage.Frame)

    fun setAllGutterIconsState(state: GutterState)

    fun setGutterIconState(visibleLine: Int, state: GutterState)
}