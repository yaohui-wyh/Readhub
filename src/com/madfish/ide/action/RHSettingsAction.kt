package com.madfish.ide.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware
import com.madfish.ide.util.Constants
import com.madfish.ide.util.RHUtil
import com.madfish.ide.view.RHIcons

/**
 * Created by Roger™
 */
class RHSettingsAction : LanguageAwareAction(
        RHUtil.message("RHSettingsAction.text"),
        RHUtil.message("RHSettingsAction.description"),
        RHIcons.SECONDARY_GROUP
), DumbAware {
    override fun actionPerformed(e: AnActionEvent?) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e?.project, Constants.Plugins.name)
    }
}