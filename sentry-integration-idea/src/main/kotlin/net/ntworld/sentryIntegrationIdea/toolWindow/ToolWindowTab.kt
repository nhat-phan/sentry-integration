package net.ntworld.sentryIntegrationIdea.toolWindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import net.ntworld.sentryIntegration.debug
import net.ntworld.sentryIntegrationIdea.Component

open class ToolWindowTab<T : Component>(
    private val toolWindow: ToolWindow,
    private val properties: Properties,
    private val componentFactory: ComponentFactory<T>
) {
    constructor(
        toolWindow: ToolWindow,
        properties: Properties,
        factory: () -> T
    ): this(toolWindow, properties, SimpleComponentFactory(factory))

    private var myTabName = properties.tabName
    private var myIsOpened: Boolean = false
    private var myContent: Content? = null
    private var myComponent: T? = null
    private val delegate = lazy { makeContent() }
    val component: T
        get()  {
            val component = myComponent
            if (null === component) {
                val created = componentFactory.make()
                myComponent = created
                return created
            }
            return component
        }
    val content by delegate
    val isOpened: Boolean
        get() = myIsOpened

    private val myStaticContentManagerListener = object : ContentManagerListener {
        override fun contentRemoved(event: ContentManagerEvent) {
            if (delegate.isInitialized() && event.content === content) {
                myIsOpened = false
                if (null !== properties.listener) {
                    properties.listener.didClose(component, false)
                }
            }
        }

        override fun contentAdded(event: ContentManagerEvent) {
            if (delegate.isInitialized() && event.content === content) {
                myIsOpened = true
                if (null !== properties.listener) {
                    properties.listener.didOpen(component, false)
                }
            }
        }
    }

    private val myDisposableContentManagerListener = object : ContentManagerListener {
        override fun contentRemoved(event: ContentManagerEvent) {
            if (event.content === myContent) {
                myIsOpened = false
                if (null !== properties.listener) {
                    properties.listener.didClose(component, true)
                }
                val content = myContent
                if (null !== content) {
                    content.dispose()
                }
                myContent = null
                myComponent = null
            }
        }

        override fun contentAdded(event: ContentManagerEvent) {
            if (event.content === myContent) {
                myIsOpened = true
                if (null !== properties.listener) {
                    properties.listener.didOpen(component, true)
                }
            }
        }
    }

    init {
        toolWindow.contentManager.addContentManagerListener(
            if (properties.disposeAfterRemoved) myDisposableContentManagerListener else myStaticContentManagerListener
        )
    }

    fun updateTabName(tabName: String) {
        myTabName = tabName
    }

    fun open() {
        if (properties.disposeAfterRemoved) {
            if (!isOpened) {
                val created = makeContent()
                myContent = created
                toolWindow.contentManager.addContent(created)
            }
            val content = myContent
            if (null !== content) {
                toolWindow.contentManager.setSelectedContent(content)
            }
        } else {
            if (!isOpened) {
                toolWindow.contentManager.addContent(this.content)
            }
            toolWindow.contentManager.setSelectedContent(this.content)
        }
    }

    fun close() {
        if (properties.disposeAfterRemoved) {
            val content = myContent
            if (null !== content && isOpened) {
                toolWindow.contentManager.removeContent(content, true)
            }
        } else {
            if (isOpened) {
                toolWindow.contentManager.removeContent(this.content, false)
            }
        }
    }

    private fun makeContent(): Content {
        val content = ContentFactory.SERVICE.getInstance().createContent(
            this.component.component,
            myTabName,
            false
        )

        val created = this.component
        if (created is Disposable) {
            debug("DISPOSER: register (content > ToolWindowTab[${properties.tabName}])")
            Disposer.register(content, created)
        }

        return content
    }

    data class Properties(
        val tabName: String,
        val disposeAfterRemoved: Boolean = false,
        val listener: Listener? = null
    )

    interface Listener {
        fun didOpen(component: Any, willBeDisposed: Boolean)

        fun didClose(component: Any, willBeDisposed: Boolean)
    }

    interface ComponentFactory<T : Component> {
        fun make(): T
    }

    private class SimpleComponentFactory<T : Component>(private val componentFactory: () -> T): ComponentFactory<T> {
        override fun make(): T {
            return componentFactory.invoke()
        }
    }
}