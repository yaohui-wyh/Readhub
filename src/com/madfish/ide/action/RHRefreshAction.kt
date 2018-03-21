package com.madfish.ide.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.madfish.ide.messages.READHUB_REFRESH_TOPIC
import com.madfish.ide.util.RHUtil

/**
 * Created by Roger™
 */
class RHRefreshAction : DumbAwareAction(
        RHUtil.message("RHRefreshAction.text"),
        RHUtil.message("RHRefreshAction.description"),
        AllIcons.Actions.Refresh
) {
    override fun actionPerformed(e: AnActionEvent?) {
        e?.project?.messageBus?.syncPublisher(READHUB_REFRESH_TOPIC)?.refreshItems(background = false)
    }

    override fun update(e: AnActionEvent?) {
        e?.presentation?.let { p ->
            p.text = RHUtil.message("RHRefreshAction.text")
            p.description = RHUtil.message("RHRefreshAction.description")
        }
    }
}