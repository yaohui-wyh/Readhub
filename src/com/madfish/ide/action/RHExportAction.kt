package com.madfish.ide.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction
import com.madfish.ide.util.Notification
import com.madfish.ide.util.RHDataKeys
import com.madfish.ide.util.RHUtil
import com.madfish.ide.view.RHIcons
import java.awt.datatransfer.StringSelection

/**
 * Created by Roger™
 */
class RHExportAction(private val provider: DataProvider) : IconWithTextAction(
        RHUtil.message("RHExportAction.text"),
        RHUtil.message("RHExportAction.description"),
        RHIcons.COPY
), DumbAware {

    override fun actionPerformed(e: AnActionEvent?) {
        RHDataKeys.tableItem.getData(provider)?.let {
            CopyPasteManager.getInstance().setContents(StringSelection("${it.getTitleText()}\n${it.getUrlText()}"))
            Notification.successBalloon(e?.project, RHUtil.message("RHExportAction.notification"))
        }
    }

    override fun update(e: AnActionEvent?) {
        e?.presentation?.let { p ->
            p.text = RHUtil.message("RHExportAction.text")
            p.description = RHUtil.message("RHExportAction.description")
            p.isEnabledAndVisible = false
            RHDataKeys.tableItem.getData(provider)?.let { p.isEnabledAndVisible = true }
        }
    }
}