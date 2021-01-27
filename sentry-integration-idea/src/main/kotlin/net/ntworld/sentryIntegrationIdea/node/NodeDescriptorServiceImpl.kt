package net.ntworld.sentryIntegrationIdea.node

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegrationIdea.serviceProvider.ProjectServiceProvider

class NodeDescriptorServiceImpl(
    private val projectServiceProvider: ProjectServiceProvider,
    private val linkedProject: LinkedProject?
) : NodeDescriptorService {
    override fun make(node: Node): PresentableNodeDescriptor<Node> {
        val presentation = MyPresentableNodeDescriptor(projectServiceProvider, linkedProject, node)
        presentation.update()
        return presentation
    }

    override fun findNode(input: Any?): Node? {
        return if (null !== input && input is MyPresentableNodeDescriptor) {
            input.element
        } else null
    }

    private class MyPresentableNodeDescriptor(
        private val projectServiceProvider: ProjectServiceProvider,
        private val linkedProject: LinkedProject?,
        private val element: Node
    ) : PresentableNodeDescriptor<Node>(projectServiceProvider.project, null) {
        override fun update(presentation: PresentationData) {
            element.updatePresentation(projectServiceProvider, linkedProject, presentation)
        }

        override fun getElement(): Node = element
    }
}