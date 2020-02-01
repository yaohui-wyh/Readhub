package com.madfish.ide.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import com.madfish.ide.util.Constants
import com.madfish.ide.view.RHConfigComponent
import javax.swing.JComponent

/**
 * Created by Roger™
 */
class RHConfigurableProvider : ConfigurableProvider() {
    override fun createConfigurable() = RHConfigurable()
}

class RHConfigurable : Configurable {

    private val component = RHConfigComponent()

    override fun disposeUIResources() {}

    override fun reset() {
        component.reset()
    }

    override fun getHelpTopic() = ""

    override fun isModified() = component.isModified()

    override fun getDisplayName() = Constants.Plugins.name

    override fun apply() {
        component.apply()
    }

    override fun createComponent(): JComponent? = component.mainPanel
}
