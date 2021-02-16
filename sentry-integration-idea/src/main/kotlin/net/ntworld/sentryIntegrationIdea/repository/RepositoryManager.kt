package net.ntworld.sentryIntegrationIdea.repository

import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace

interface RepositoryManager {

    fun isSourceFile(linkedProject: LinkedProject, stacktrace: SentryEventExceptionStacktrace): Boolean

    fun isSourceFile(linkedProject: LinkedProject, frame: Storage.Frame): Boolean

    fun findLocalFilePath(linkedProject: LinkedProject, frame: Storage.Frame): FilePath

    fun findVcsVirtualFile(linkedProject: LinkedProject, frame: Storage.Frame): VirtualFile?

}