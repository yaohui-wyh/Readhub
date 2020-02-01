package com.madfish.ide.component

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.madfish.ide.configurable.RHData
import com.madfish.ide.configurable.RHSettings
import com.madfish.ide.util.Constants
import com.madfish.ide.util.RHApi
import org.apache.commons.lang.RandomStringUtils

/**
 * Created by Roger™
 */
class RHAppComponent : ApplicationComponent {
    override fun getComponentName() = Constants.Components.appName

    override fun disposeComponent() {}

    override fun initComponent() {
        val settings = RHSettings.instance
        if (settings.uuid.isBlank()) {
            settings.uuid = RandomStringUtils.randomAlphanumeric(8)
        }

        ApplicationManager.getApplication().executeOnPooledThread {
            RHData.instance.reduceCachedItems()
            RHApi.refreshAll()
            settings.setRefreshTimer()
        }
    }
}