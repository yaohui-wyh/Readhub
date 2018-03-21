package com.madfish.ide.component

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.madfish.ide.util.RHApi
import com.madfish.ide.configurable.RHSettings
import com.madfish.ide.util.Constants

/**
 * Created by Roger™
 */
class RHAppComponent : ApplicationComponent {
    override fun getComponentName() = Constants.COMPONENT_NAME

    override fun disposeComponent() {}

    override fun initComponent() {
        ApplicationManager.getApplication().executeOnPooledThread { RHApi.refreshAll() }
        RHSettings.instance.init()
    }
}