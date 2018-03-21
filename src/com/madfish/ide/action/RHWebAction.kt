package com.madfish.ide.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.madfish.ide.model.RHCategory
import com.madfish.ide.util.Constants
import com.madfish.ide.util.RHUtil
import com.madfish.ide.view.RHIcons

/**
 * Created by Roger™
 */
class RHWebAction(private val category: RHCategory) : DumbAwareAction(
        RHUtil.message("RHWebAction.text"),
        RHUtil.message("RHWebAction.description"),
        RHIcons.READHUB
) {
    override fun actionPerformed(e: AnActionEvent?) {
        BrowserUtil.browse("${Constants.READHUB_HOST}/${category.path}")
    }

    override fun update(e: AnActionEvent?) {
        e?.presentation?.let { p ->
            p.text = RHUtil.message("RHWebAction.text")
            p.description = RHUtil.message("RHWebAction.description")
        }
    }
}