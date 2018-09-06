package com.madfish.ide.view

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.model.RHTopic
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Created by Roger™
 */
class RHTopicWindowContent(project: Project) : RHToolWindowContent(project, RHCategory.TOPIC) {

    override fun initTableStyle() {
        super.initTableStyle()
        myTable.tableHeader = null
    }

    override fun setLinkContent(parent: JPanel, item: RHBaseItem?) {
        if (item is RHTopic) {
            item.newsArray.map { it.duplicateId }.toSet().forEach { id ->
                val news = item.newsArray.filter { it.duplicateId == id }
                if (news.isNotEmpty()) {
                    val itemPanel = JPanel(HorizontalLayout(5))
                    val label = HyperlinkLabel(news[0].title, UIUtil.getLabelForeground(), UIUtil.getLabelBackground(), UIUtil.getLabelForeground())
                    label.font = JBUI.Fonts.smallFont()
                    label.addHyperlinkListener { news[0].url?.let { url -> BrowserUtil.browse(url) } }
                    itemPanel.add(label)
                    itemPanel.background = background
                    news.forEach { item ->
                        val siteLabel = HyperlinkLabel(item.siteName)
                        siteLabel.addHyperlinkListener { item.url?.let { url -> BrowserUtil.browse(url) } }
                        siteLabel.font = JBUI.Fonts.smallFont().deriveFont(Font.BOLD)
                        itemPanel.add(siteLabel)
                        if (item != news.last()) {
                            itemPanel.add(JLabel("/"))
                        }
                    }
                    parent.add(itemPanel)
                }
            }
        }
    }
}