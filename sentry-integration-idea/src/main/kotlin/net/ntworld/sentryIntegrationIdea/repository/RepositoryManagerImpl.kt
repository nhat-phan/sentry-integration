package net.ntworld.sentryIntegrationIdea.repository

import com.intellij.dvcs.repo.Repository
import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.LocalFilePath
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsRootChecker
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.history.FileHistoryUtil
import com.intellij.vcs.log.history.VcsLogFileRevision
import com.intellij.vcs.log.impl.VcsProjectLog
import com.intellij.vcs.log.util.VcsLogUtil
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.toCrossPlatformsPath
import java.nio.file.Paths

class RepositoryManagerImpl(private val project: Project) : RepositoryManager {
    private val myRepositories = mutableMapOf<String, Repository?>()
    private val myRootCheckers = mutableMapOf<String, VcsRootChecker>()
    private val myCheckSourceFiles = mutableMapOf<String, Boolean>()

    override fun isSourceFile(linkedProject: LinkedProject, path: String): Boolean {
        val key = linkedProject.id + ":" + path
        if (myCheckSourceFiles.containsKey(key)) {
            return myCheckSourceFiles[key]!!
        }

        val (repository, rootChecker) = findVcsRootChecker(linkedProject)
        if (null === repository || null === rootChecker) {
            return false
        }

        val filePath = findLocalFilePath(linkedProject.localRootPath, path)
        val virtualFile = filePath.virtualFile
        if (null === virtualFile) {
            return false
        }

        val created = !rootChecker.isIgnored(repository.root, virtualFile)
        myCheckSourceFiles[key] = created
        return created
    }

    override fun findLocalFilePath(linkedProject: LinkedProject, path: String): FilePath {
        return findLocalFilePath(linkedProject.localRootPath, path)
    }

    override fun findVcsVirtualFile(linkedProject: LinkedProject, path: String): VirtualFile? {
        val vcsRepositoryManager = findVcsRepositoryManager(linkedProject)

        if (null === vcsRepositoryManager) return null

        val vcsProjectLog = VcsProjectLog.getInstance(project)
        if (null === vcsProjectLog) return null

        val vcsLogData = vcsProjectLog.dataManager
        if (null === vcsLogData) return null

        val logProvider = vcsLogData.getLogProvider(vcsRepositoryManager.root)
        val vcsLogDiffHandler = logProvider.diffHandler
        if (null === vcsLogDiffHandler) return null

        val branch = VcsLogUtil.findBranch(vcsLogData.dataPack.refsModel, vcsRepositoryManager.root, linkedProject.deployedBranch)
        if (null === branch) return null

        val vcsFullCommitDetails = VcsLogUtil.getDetails(vcsLogData, vcsRepositoryManager.root, branch.commitHash)
        val filePath = findLocalFilePath(linkedProject.localRootPath, path)
        return FileHistoryUtil.createVcsVirtualFile(VcsLogFileRevision(
            vcsFullCommitDetails,
            vcsLogDiffHandler.createContentRevision(
                filePath,
                vcsFullCommitDetails.id
            ),
            filePath,
            false
        ))
    }

    private fun findVcsRepositoryManager(linkedProject: LinkedProject): Repository? {
        if (myRepositories.containsKey(linkedProject.id)) {
            return myRepositories[linkedProject.id]
        }

        val vcsRepositoryManager = VcsRepositoryManager.getInstance(project)
        for (item in vcsRepositoryManager.repositories) {
            if (item.root.path == linkedProject.localRootPath) {
                myRepositories[linkedProject.id] = item
                return item
            }
        }

        myRepositories[linkedProject.id] = null
        return myRepositories[linkedProject.id]
    }

    private fun findVcsRootChecker(linkedProject: LinkedProject): Pair<Repository?, VcsRootChecker?> {
        val repository = findVcsRepositoryManager(linkedProject)
        if (null === repository) {
            return Pair(null, null)
        }

        if (myRootCheckers.containsKey(linkedProject.id)) {
            return Pair(repository, myRootCheckers[linkedProject.id])
        }

        val myVcsManager = ProjectLevelVcsManager.getInstance(project)
        val rootChecker = myVcsManager.getRootChecker(repository.vcs)
        myRootCheckers[linkedProject.id] = rootChecker
        return Pair(repository, myRootCheckers[linkedProject.id])
    }

    private fun findLocalFilePath(localRootPath: String, path: String): FilePath {
        return LocalFilePath(Paths.get(localRootPath, path).toString().toCrossPlatformsPath(), false)
    }
}