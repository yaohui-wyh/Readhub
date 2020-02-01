package com.madfish.ide.configurable

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.madfish.ide.internal.d
import com.madfish.ide.messages.READHUB_REFRESH_TOPIC
import com.madfish.ide.view.LanguageItem
import com.madfish.ide.view.RefreshRanges
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by Roger™
 */
@State(name = "readhubSettings", storages = [(Storage("readhub/readhub-settings.xml"))])
class RHSettings : PersistentStateComponent<RHSettings.State> {
    private var myState = State()
    private var task: ScheduledFuture<*>? = null
    private val logger = Logger.getInstance(this::class.java)

    companion object {
        val instance: RHSettings
            get() = ServiceManager.getService(RHSettings::class.java)
    }

    var refreshMode: RefreshRanges
        get() = myState.refreshMode
        set(mode) {
            myState.refreshMode = mode
        }

    var lang: LanguageItem
        get() = myState.lang
        set(lang) {
            myState.lang = lang
        }

    var uuid: String
        get() = myState.pluginId
        set(uuid) {
            myState.pluginId = uuid
        }

    override fun getState() = myState

    override fun loadState(state: State) {
        myState = state
    }

    @Synchronized
    fun setRefreshTimer() {
        task?.cancel(true)
        task = if (refreshMode.minutes < 0) {
            null
        } else {
            JobScheduler.getScheduler().scheduleWithFixedDelay({
                val app = ApplicationManager.getApplication()
                app.invokeLater {
                    logger.d("auto refresh triggered")
                    app.messageBus.syncPublisher(READHUB_REFRESH_TOPIC).refreshItems()
                }
            }, refreshMode.minutes.toLong(), refreshMode.minutes.toLong(), TimeUnit.MINUTES)
        }
    }

    class State {
        var refreshMode: RefreshRanges = RefreshRanges.NONE
        var lang: LanguageItem = LanguageItem.CHINESE
        var pluginId: String = ""
    }
}