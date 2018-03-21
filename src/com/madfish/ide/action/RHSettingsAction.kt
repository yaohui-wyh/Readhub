package com.madfish.ide.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction
import com.madfish.ide.util.Constants
import com.madfish.ide.util.RHUtil

/**
 * Created by Roger™
 */
class RHSettingsAction : DumbAwareAction(
        RHUtil.message("RHSettingsAction.text"),
        RHUtil.message("RHSettingsAction.description"),
        AllIcons.General.SecondaryGroup
) {
    override fun actionPerformed(e: AnActionEvent?) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e?.project, Constants.PLUGIN_NAME)
    }

    override fun update(e: AnActionEvent?) {
        e?.presentation?.let { p ->
            p.text = RHUtil.message("RHSettingsAction.text")
            p.description = RHUtil.message("RHSettingsAction.description")
        }
    }
}