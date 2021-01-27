package net.ntworld.sentryIntegrationIdea.editor

import com.intellij.openapi.fileEditor.TextEditor
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LinkedProject

/**
 * Purpose of EditorManager is fetching content of a stacktrace from vcs repository
 * and compare the content, if everything is okay, it's just create an EditorController instance
 * and pass the task to the controller.
 *
 * EditorManager will be a singleton, bound to ProjectServiceProvider.
 *
 * EditorManager will use ExceptionStorage to fetch stacktrace/exception
 * There are 2 kind of storage:
 *   - storage for all action requested from ToolWindow (main UI)
 *   - storage for worker (implement later)
 * Maybe another kind storage is combine 2 kinds above
 */
interface EditorManager {
    /**
     * Open and jump to a editor in line and display a frame
     */
    fun open(frame: Storage.Frame)
}