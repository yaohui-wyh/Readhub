package com.madfish.ide.component

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.madfish.ide.configurable.RHData
import com.madfish.ide.configurable.RHSettings
import com.madfish.ide.util.RHApi
import org.apache.commons.lang.RandomStringUtils

/**
 * Created by Roger™
 */
class RHAppComponent : StartupActivity.Background {

    override fun runActivity(project: Project) {
        val settings = service<RHSettings>()
        if (settings.uuid.isBlank()) {
            settings.uuid = RandomStringUtils.randomAlphanumeric(8)
        }

        ApplicationManager.getApplication().executeOnPooledThread {
            service<RHData>().reduceCachedItems()
            RHApi.refreshAll()
            settings.setRefreshTimer()
        }
    }
}