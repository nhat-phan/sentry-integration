package net.ntworld.sentryIntegrationIdea.repository

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.LocalFilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import net.ntworld.sentryIntegration.InstanceCache
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.entity.SentryEventExceptionStacktrace
import net.ntworld.sentryIntegration.toCrossPlatformsPath
import java.nio.file.Paths

class CompiledLanguageWrapper(
    private val project: Project,
    private val repositoryManager: RepositoryManagerImpl
) : RepositoryManager {
    private val myPhysicalPaths = InstanceCache<String, String>()

    override fun isSourceFile(linkedProject: LinkedProject, stacktrace: SentryEventExceptionStacktrace): Boolean {
        return doCheckSourceFile(linkedProject, stacktrace.absolutePath.value, stacktrace.module)
    }

    override fun isSourceFile(linkedProject: LinkedProject, frame: Storage.Frame): Boolean {
        return doCheckSourceFile(linkedProject, frame.path, frame.module)
    }

    private fun doCheckSourceFile(linkedProject: LinkedProject, path: String, module: String): Boolean {
        if (path.isEmpty()) {
            return false
        }

        // We have to introduce cached
        val resolvedPath = resolvePhysicalPath(linkedProject, path, module)

        if (resolvedPath.isEmpty()) {
            return false
        }

        val filePath = LocalFilePath(resolvedPath, false)
        return repositoryManager.checkSourceFileByFilePath(linkedProject, filePath)
    }

    private fun resolvePhysicalPath(linkedProject: LinkedProject, path: String, module: String): String {
        if (path.isEmpty()) {
            return path
        }

        val key = linkedProject.id + ":" + path + "/" + module
        return myPhysicalPaths.get(key) {
            val virtualFile = resolveVirtualFile(linkedProject, path, module)
            if (null === virtualFile) {
                ""
            } else {
                virtualFile.path
            }
        }
    }

    private fun resolveVirtualFile(linkedProject: LinkedProject, path: String, module: String): VirtualFile? {
        val psiFiles = FilenameIndex.getFilesByName(project, path, GlobalSearchScope.allScope(project))

        if (psiFiles.isEmpty()) {
            return null
        }

        return matchPsiFilesByModule(linkedProject, psiFiles, module).virtualFile
    }


    private fun matchPsiFilesByModule(linkedProject: LinkedProject, psiFiles: Array<PsiFile>, module: String): PsiFile {
        if (psiFiles.count() == 1) {
            return psiFiles.first()
        }

        for (psiFile in psiFiles) {
            if (PsiFileMatcher.isMatched(linkedProject, psiFile, module)) {
                return psiFile
            }
        }
        return psiFiles.first()
    }

    override fun findLocalVirtualFile(linkedProject: LinkedProject, frame: Storage.Frame): VirtualFile? {
        return resolveVirtualFile(linkedProject, frame.path, frame.module)
    }

    override fun findLocalFilePath(linkedProject: LinkedProject, frame: Storage.Frame): FilePath {
        val physicalPath = resolvePhysicalPath(linkedProject, frame.path, frame.module)

        return LocalFilePath(physicalPath, false)
    }

    override fun findVcsVirtualFile(linkedProject: LinkedProject, frame: Storage.Frame): VirtualFile? {
        val filePath = findLocalFilePath(linkedProject, frame)

        return repositoryManager.findVcsVirtualFileByFilePath(linkedProject, filePath)
    }
}