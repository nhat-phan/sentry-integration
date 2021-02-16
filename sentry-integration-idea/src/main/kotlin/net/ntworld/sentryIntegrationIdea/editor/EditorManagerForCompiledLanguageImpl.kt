package net.ntworld.sentryIntegrationIdea.editor

import com.intellij.psi.impl.search.runSearch
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchRequestQuery
import com.intellij.psi.search.searches.ReferencesSearch
import net.ntworld.sentryIntegration.Storage
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class EditorManagerForCompiledLanguageImpl(
    private val projectServiceProvider: ProjectServiceProvider
) : EditorManager {
    override fun open(frame: Storage.Frame) {
//        runSearch(
//            projectServiceProvider.project,
//            SearchRequestQuery(projectServiceProvider.project, ))

        // ReferencesSearch.search()
        println(frame)
        val a = FilenameIndex.getFilesByName(projectServiceProvider.project, frame.module, GlobalSearchScope.allScope(projectServiceProvider.project))
        println(a)
        val b = FilenameIndex.getFilesByName(projectServiceProvider.project, frame.path, GlobalSearchScope.allScope(projectServiceProvider.project))
        println(b)
        val c = FilenameIndex.getFilesByName(projectServiceProvider.project, frame.module.replace(".", "/"), GlobalSearchScope.allScope(projectServiceProvider.project))
        println(c)
    }
}