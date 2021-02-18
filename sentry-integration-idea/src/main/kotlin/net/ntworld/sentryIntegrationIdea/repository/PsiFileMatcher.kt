package net.ntworld.sentryIntegrationIdea.repository

import com.intellij.psi.PsiFile
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.toCrossPlatformsPath

object PsiFileMatcher {
    fun isMatched(linkedProject: LinkedProject, psiFile: PsiFile, module: String): Boolean {
        // let's do simple guess by path first
        return guessByPathAndNamespace(linkedProject, psiFile, module)
    }

    private fun guessByPathAndNamespace(linkedProject: LinkedProject, psiFile: PsiFile, module: String): Boolean {
        val info = parseModuleInfo(module)
        val virtualFile = psiFile.virtualFile
        val absPath = if (null === virtualFile) "" else virtualFile.path
        val path = absPath.replace(linkedProject.localRootPath, "").toCrossPlatformsPath().replace('/', '.')

        return path.indexOf(info.namespace) > 0
    }

    fun parseModuleInfo(module: String): ModuleInfo {
        val indexOfDollarSign = module.indexOf('$')
        if (indexOfDollarSign > 0) {
            return parseModuleInfo(module.substring(0, indexOfDollarSign))
        }
        val parts = module.split('.')
        if (parts.size == 1) {
            return ModuleInfo("", parts[0])
        }
        return ModuleInfo(
            namespace = parts.subList(0, parts.lastIndex).joinToString("."),
            className = parts[parts.lastIndex]
        )
    }

    data class ModuleInfo(
        val namespace: String,
        val className: String
    )
}