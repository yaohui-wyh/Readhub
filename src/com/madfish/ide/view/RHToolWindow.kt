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
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import com.madfish.ide.internal.d
import com.madfish.ide.messages.READHUB_REFRESH_TOPIC
import com.madfish.ide.messages.READHUB_VIEW_TOPIC
import com.madfish.ide.messages.RefreshListener
import com.madfish.ide.messages.TableViewListener
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.util.ErrMessage
import com.madfish.ide.util.Notification
import com.madfish.ide.util.RHApi
import com.madfish.ide.util.RHUtil

data class NameContentPair(val category: RHCategory, val content: RHToolWindowContent)

/**
 * Created by Roger™
 */
class RHToolWindow : ToolWindowFactory, DumbAware {

    private lateinit var myToolWindow: ToolWindow
    private val logger = Logger.getInstance(this::class.java)
    private val contentFactory = ContentFactory.SERVICE.getInstance()
    private val myRHContents = mutableListOf<NameContentPair>()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        myToolWindow = toolWindow
        if (project.isOpen) {
            initRHContents(project)
            subscribeRHListener(project)
            addContentListener(project)
        }
    }

    private fun initRHContents(project: Project) {
        myRHContents.clear()

        myRHContents.add(NameContentPair(RHCategory.TOPIC, RHTopicWindowContent(project)))
        myRHContents.add(NameContentPair(RHCategory.NEWS, RHNewsWindowContent(project, RHCategory.NEWS)))
        myRHContents.add(NameContentPair(RHCategory.TECH_NEWS, RHNewsWindowContent(project, RHCategory.TECH_NEWS)))
        myRHContents.add(NameContentPair(RHCategory.BLOCKCHAIN, RHNewsWindowContent(project, RHCategory.BLOCKCHAIN)))
        myRHContents.add(NameContentPair(RHCategory.JOB, RHJobWindowContent(project)))

        myRHContents.forEach { (category, content) ->
            content.initView()
            val obj = contentFactory.createContent(content.createToolWindow(), category.getName(), false)
            myToolWindow.contentManager.addContent(obj)
        }
    }

    private fun doRefresh(category: RHCategory?) {
        if (category == null) {
            val apiResult = RHApi.refreshAll()
            if (apiResult.success) {
                updateTableIfMatch(ignoreMatch = true)
            }
        } else {
            val apiResult = RHApi.fetchLatestItems(category)
            if (apiResult.success) {
                updateTableIfMatch(category.getName())
            }
        }
    }

    private fun subscribeRHListener(project: Project) {
        val busConnection = project.messageBus.connect(project)
        busConnection.subscribe(READHUB_REFRESH_TOPIC, object : RefreshListener {
            override fun refreshItems(category: RHCategory?, background: Boolean) {
                if (background) {
                    ApplicationManager.getApplication().executeOnPooledThread { doRefresh(category) }
                } else {
                    ProgressManager.getInstance().run(object : Task.Backgroundable(project, RHUtil.message("View.loading")) {
                        override fun run(indicator: ProgressIndicator) {
                            doRefresh(category)
                        }
                    })
                }
            }

            override fun loadPrevItems(category: RHCategory) {
                ProgressManager.getInstance().run(object : Task.Backgroundable(project, RHUtil.message("View.loading")) {
                    override fun run(indicator: ProgressIndicator) {
                        val apiResult = RHApi.fetchPrevItems(category)
                        if (!apiResult.success) {
                            logger.d(apiResult.errorMessage)
                            Notification.errorBalloon(project, ErrMessage.API_NETWORK_ERROR.text)
                        }
                        updateTableIfMatch(category.getName())
                    }
                })
            }
        })

        busConnection.subscribe(READHUB_VIEW_TOPIC, object : TableViewListener {
            override fun onItemClicked(name: String, obj: RHBaseItem?) {
                val selected = myToolWindow.contentManager.selectedContent ?: return
                val selectedPair = myRHContents.find { it.category.getName() == selected.displayName } ?: return
                selectedPair.content.onItemClicked(name, obj)
            }
        })
    }

    private fun updateTableIfMatch(displayName: String = "", ignoreMatch: Boolean = false) {
        ApplicationManager.getApplication().invokeLater {
            val currentContent = myToolWindow.contentManager.selectedContent ?: return@invokeLater
            if (currentContent.displayName != displayName && !ignoreMatch) return@invokeLater
            val selectedPair = myRHContents.find { it.category.getName() == currentContent.displayName } ?: return@invokeLater
            selectedPair.content.updateTable()
        }
    }

    private fun addContentListener(project: Project) {
        myToolWindow.contentManager.addContentManagerListener(object : ContentManagerListener {
            override fun selectionChanged(event: ContentManagerEvent) {
                if (event.operation == ContentManagerEvent.ContentOperation.add) {
                    val displayName = event.content.displayName ?: return
                    val selectedPair = myRHContents.find { it.category.getName() == displayName } ?: return
                    project.messageBus.syncPublisher(READHUB_REFRESH_TOPIC).refreshItems(selectedPair.category)
                }
            }
        })
    }
}