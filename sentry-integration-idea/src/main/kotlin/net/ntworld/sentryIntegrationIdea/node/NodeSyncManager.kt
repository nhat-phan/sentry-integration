package net.ntworld.sentryIntegrationIdea.node

interface NodeSyncManager {
    fun sync(root: RootNode, tree: SyncedTree)
}