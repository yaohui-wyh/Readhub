package com.madfish.ide.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.madfish.ide.messages.READHUB_REFRESH_TOPIC
import com.madfish.ide.util.RHUtil
import com.madfish.ide.view.RHIcons

/**
 * Created by Roger™
 */
class RHRefreshAction : LanguageAwareAction(
        RHUtil.message("RHRefreshAction.text"),
        RHUtil.message("RHRefreshAction.description"),
        RHIcons.REFRESH
), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        e.project?.messageBus?.syncPublisher(READHUB_REFRESH_TOPIC)?.refreshItems(background = false)
    }
}