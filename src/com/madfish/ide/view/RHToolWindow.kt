package com.madfish.ide.view

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManagerAdapter
import com.intellij.ui.content.ContentManagerEvent
import com.madfish.ide.configurable.RHData
import com.madfish.ide.messages.READHUB_REFRESH_TOPIC
import com.madfish.ide.messages.READHUB_VIEW_TOPIC
import com.madfish.ide.messages.RefreshListener
import com.madfish.ide.model.RHCategory
import com.madfish.ide.util.*

/**
 * Created by Roger™
 */
class RHToolWindow : ToolWindowFactory, DumbAware {

    private lateinit var toolWindow: ToolWindow
    private val logger = Logger.getInstance(this::class.java)
    private val contentFactory = ContentFactory.SERVICE.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        this.toolWindow = toolWindow
        subscribeRHListener(project)
        addRHTopicContent(project)
        addContents(project, listOf(RHCategory.NEWS, RHCategory.TECH_NEWS, RHCategory.BLOCKCHAIN))
        addRHJobContent(project)
        addContentListener(project)
    }

    private fun addRHTopicContent(project: Project) {
        val content = RHTopicWindowContent(project)
        val contentObj = contentFactory.createContent(content.createToolWindow(), RHCategory.TOPIC.getName(), false)
        toolWindow.contentManager.addContent(contentObj)
    }

    private fun addRHJobContent(project: Project) {
        val content = RHJobWindowContent(project)
        val contentObj = contentFactory.createContent(content.createToolWindow(), RHCategory.JOB.getName(), false)
        toolWindow.contentManager.addContent(contentObj)
    }

    private fun addContents(project: Project, categories: List<RHCategory>) {
        categories.forEach {
            val content = RHNewsWindowContent(project, it)
            val contentObj = contentFactory.createContent(content.createToolWindow(), it.getName(), false)
            toolWindow.contentManager.addContent(contentObj)
        }
    }

    private fun subscribeRHListener(project: Project) {
        val busConnection = project.messageBus.connect(project)
        val displayName = toolWindow.contentManager.selectedContent?.displayName.orEmpty()
        busConnection.subscribe(READHUB_REFRESH_TOPIC, object : RefreshListener {
            override fun refreshItems(category: RHCategory?, background: Boolean) {
                if (background) {
                    ApplicationManager.getApplication().executeOnPooledThread {
                        if (category == null) RHApi.refreshAll() else RHApi.fetchLatestItems(category)
                        ApplicationManager.getApplication().invokeLater {
                            project.messageBus.syncPublisher(READHUB_VIEW_TOPIC).updateTable(displayName)
                        }
                    }
                } else {
                    ProgressManager.getInstance().run(object : Task.Backgroundable(project, RHUtil.message("View.loading")) {
                        override fun run(indicator: ProgressIndicator) {
                            if (category == null) RHApi.refreshAll() else RHApi.fetchLatestItems(category)
                            ApplicationManager.getApplication().invokeLater {
                                project.messageBus.syncPublisher(READHUB_VIEW_TOPIC).updateTable(displayName)
                            }
                        }
                    })
                }
            }

            override fun loadPrevItems(category: RHCategory) {
                ProgressManager.getInstance().run(object : Task.Backgroundable(project, RHUtil.message("View.loading")) {
                    override fun run(indicator: ProgressIndicator) {
                        val apiResult = RHApi.fetchPrevItems(category)
                        ApplicationManager.getApplication().invokeLater {
                            if (!apiResult.success) {
                                logger.d(apiResult.errorMessage)
                                Notification.errorBalloon(project, ErrMessage.API_NETWORK_ERROR.text)
                            }
                            project.messageBus.syncPublisher(READHUB_VIEW_TOPIC).updateTable(displayName)
                        }
                    }
                })
            }

            override fun clearItems() {
                RHData.instance.clearCache()
                project.messageBus.syncPublisher(READHUB_VIEW_TOPIC).updateTable(displayName)
            }
        })
    }

    private fun addContentListener(project: Project) {
        toolWindow.contentManager.addContentManagerListener(object : ContentManagerAdapter() {
            override fun selectionChanged(event: ContentManagerEvent?) {
                val displayName = event?.content?.displayName
                displayName?.let { name ->
                    RHCategory.values().find { it.getName() == name }?.let { category ->
                        project.messageBus.syncPublisher(READHUB_REFRESH_TOPIC).refreshItems(category)
                    }
                }
                project.messageBus.syncPublisher(READHUB_VIEW_TOPIC).updateTable(displayName.orEmpty())
            }
        })
    }
}
