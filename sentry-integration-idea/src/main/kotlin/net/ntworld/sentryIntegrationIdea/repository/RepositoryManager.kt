package net.ntworld.sentryIntegrationIdea.repository

import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import net.ntworld.sentryIntegration.entity.LinkedProject

interface RepositoryManager {

    fun isSourceFile(linkedProject: LinkedProject, path: String): Boolean

    fun findLocalFilePath(linkedProject: LinkedProject, path: String): FilePath

    fun findVcsVirtualFile(linkedProject: LinkedProject, path: String): VirtualFile?

}