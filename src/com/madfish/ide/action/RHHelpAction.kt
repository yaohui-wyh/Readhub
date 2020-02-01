package com.madfish.ide.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.MultiLineTooltipUI
import com.madfish.ide.util.RHUtil
import com.madfish.ide.view.RHIcons
import javax.swing.JToolTip

/**
 * Created by Roger™
 */
class RHHelpAction : LanguageAwareAction(
        RHUtil.message("RHHelpAction.text"),
        RHUtil.message("RHHelpAction.description"),
        RHIcons.INTENTION_BULB
), DumbAware {

    override fun actionPerformed(e: AnActionEvent?) {
        val text = """  Shortcuts
  ---------------------
  j -> Down
  k -> Up
  r -> Refresh
  / -> Search
  Enter -> Instant View  """
        val tip = object : JToolTip() {
            init {
                tipText = text
                setUI(MultiLineTooltipUI())
            }
        }
        val currentComponent = e?.inputEvent?.component ?: return
        JBPopupFactory.getInstance().createComponentPopupBuilder(tip, tip)
                .setRequestFocus(true)
                .setFocusable(true)
                .setResizable(false)
                .setMovable(false)
                .setModalContext(false)
                .setShowShadow(true)
                .setShowBorder(true)
                .setCancelKeyEnabled(true)
                .setCancelOnClickOutside(true)
                .setCancelOnOtherWindowOpen(true)
                .createPopup()
                .showUnderneathOf(currentComponent)
    }
}
