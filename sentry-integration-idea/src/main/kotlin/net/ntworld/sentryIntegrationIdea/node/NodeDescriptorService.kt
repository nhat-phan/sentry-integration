package net.ntworld.sentryIntegrationIdea.node

import com.intellij.ide.util.treeView.PresentableNodeDescriptor

interface NodeDescriptorService {
    fun make(node: Node): PresentableNodeDescriptor<Node>

    fun findNode(input: Any?): Node?
}