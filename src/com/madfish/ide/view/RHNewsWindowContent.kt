package com.madfish.ide.view

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.model.RHNews
import javax.swing.JPanel

/**
 * Created by Roger™
 */
class RHNewsWindowContent(project: Project, category: RHCategory) : RHToolWindowContent(project, category) {

    override fun initTableStyle() {
        super.initTableStyle()
        myTable.tableHeader = null
    }

    override fun setLinkContent(parent: JPanel, item: RHBaseItem?) {
        if (item is RHNews) {
            val itemPanel = JPanel(HorizontalLayout(5))
            var labelText = item.siteName
            if (item.authorName.isNotBlank()) {
                labelText = "$labelText / ${item.authorName}"
            }
            val label = HyperlinkLabel(labelText, UIUtil.getLabelForeground(), UIUtil.getLabelBackground(), UIUtil.getLabelForeground())
            label.font = JBUI.Fonts.smallFont()
            label.addHyperlinkListener {
                val url = item.url.trim()
                if (url.isNotBlank()) {
                    BrowserUtil.browse(url.trim())
                }
            }
            itemPanel.add(label)
            itemPanel.background = background
            parent.add(itemPanel)
        }
    }
}