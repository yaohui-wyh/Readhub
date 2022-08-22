package com.madfish.ide.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction
import com.madfish.ide.model.RHTopic
import com.madfish.ide.util.*
import com.madfish.ide.view.InstantViewDialog
import com.madfish.ide.view.RHIcons

/**
 * Created by Roger™
 */
class RHInstantViewAction(private val provider: DataProvider) : IconWithTextAction(
        RHUtil.message("RHInstantViewAction.text"),
        RHUtil.message("RHInstantViewAction.description"),
        RHIcons.PREVIEW
), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        RHDataKeys.tableItem.getData(provider)?.let { item ->
            if (item is RHTopic && item.extra.instantView) {
                ProgressManager.getInstance().run(object : Task.Backgroundable(e.project, RHUtil.message("RHInstantViewAction.progress"), false) {
                    override fun run(indicator: ProgressIndicator) {
                        val apiRet = RHApi.getInstantView(item.id)
                        if (apiRet.success && apiRet.result != null && !apiRet.result?.content.isNullOrBlank()) {
                            ApplicationManager.getApplication().invokeLater {
                                InstantViewDialog(e.project, apiRet.result!!).show()
                            }
                        } else {
                            Notification.errorBalloon(e.project, ErrMessage.INSTANT_VIEW_ERROR.text)
                        }
                    }
                })
            }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.let { p ->
            p.text = RHUtil.message("RHInstantViewAction.text")
            p.description = RHUtil.message("RHInstantViewAction.description")
            p.isEnabledAndVisible = false
            RHDataKeys.tableItem.getData(provider)?.let { item ->
                p.isEnabledAndVisible = item is RHTopic && item.extra.instantView
            }
        }
    }
}
