package com.madfish.ide.util

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.extensions.PluginId

/**
 * Created by Roger™
 */
class IdeVersionUtil {

    companion object {

        fun getIdeVersion(): String {
            val instance = ApplicationInfo.getInstance()
            return "${instance.fullVersion}_${instance.build}"
        }

        fun getPluginVersion() = PluginManager.getPlugin(PluginId.getId(Constants.PLUGIN_ID))?.version.orEmpty()
    }
}