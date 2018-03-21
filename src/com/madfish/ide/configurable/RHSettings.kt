package com.madfish.ide.configurable

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
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

    companion object {
        val instance: RHSettings
            get() = ServiceManager.getService(RHSettings::class.java)
    }

    var refreshMode: RefreshRanges
        get() = myState.refreshMode
        set(mode) {
            myState.refreshMode = mode
            // setRefreshTimer()
        }

    var lang: LanguageItem
        get() = myState.lang
        set(lang) {
            myState.lang = lang
        }

    override fun getState() = myState

    override fun loadState(state: State) {
        myState = state
    }

    fun init() {
        // setRefreshTimer()
    }

    private fun setRefreshTimer() {
        task?.cancel(true)
        task = if (refreshMode.minutes < 0) {
            null
        } else {
            JobScheduler.getScheduler().scheduleWithFixedDelay({
                val app = ApplicationManager.getApplication()
                app.invokeLater { app.messageBus.syncPublisher(READHUB_REFRESH_TOPIC).refreshItems() }
            }, refreshMode.minutes.toLong(), refreshMode.minutes.toLong(), TimeUnit.MINUTES)
        }
    }

    class State {
        var refreshMode: RefreshRanges = RefreshRanges.NONE
        var lang: LanguageItem = LanguageItem.CHINESE
    }
}