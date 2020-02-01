package com.madfish.ide.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.madfish.ide.model.RHCategory
import com.madfish.ide.util.Constants
import com.madfish.ide.util.RHUtil
import com.madfish.ide.view.RHIcons

/**
 * Created by Roger™
 */
class RHWebAction(private val category: RHCategory) : LanguageAwareAction(
        RHUtil.message("RHWebAction.text"),
        RHUtil.message("RHWebAction.description"),
        RHIcons.READHUB
), DumbAware {
    override fun actionPerformed(e: AnActionEvent?) {
        BrowserUtil.browse("${Constants.Readhub.webUrl}/${category.path}")
    }
}