package com.madfish.ide.view

import com.intellij.ide.BrowserUtil
import com.intellij.ide.IdeTooltip
import com.intellij.ide.IdeTooltipManager
import com.intellij.openapi.project.Project
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.model.RHTopic
import com.madfish.ide.util.RHUtil
import java.awt.Font
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

/**
 * Created by Roger™
 */
class RHTopicWindowContent(project: Project) : RHToolWindowContent(project, RHCategory.TOPIC) {

    val hintColumn = 1

    override fun initTableStyle() {
        super.initTableStyle()
        myTable.tableHeader = null
        myTable.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val point = e.point
                val column = myTable.columnAtPoint(point)
                if (column != hintColumn) {
                    return
                }
                val component = JLabel(RHUtil.message("RHTopicTable.hint"))
                IdeTooltipManager.getInstance().show(IdeTooltip(myTable, point, component), false)
            }
        })
    }

    override fun getColumns(): Array<ColumnInfo<RHBaseItem, *>> {
        val columns = super.getColumns().toMutableList()
        columns.add(hintColumn, object : ColumnInfo<RHBaseItem, Icon>("extra") {
            override fun valueOf(item: RHBaseItem?): Icon? = if (item is RHTopic && item.extra.instantView) RHIcons.PREVIEW else null
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer = RHIconCellRenderer()
            override fun getWidth(table: JTable?) = JBUI.scale(32)
        })
        return columns.toTypedArray()
    }

    override fun setLinkContent(parent: JPanel, item: RHBaseItem?) {
        if (item is RHTopic) {
            item.newsArray.map { it.duplicateId }.toSet().forEach { id ->
                val news = item.newsArray.filter { it.duplicateId == id }
                if (news.isNotEmpty()) {
                    val itemPanel = JPanel(HorizontalLayout(5))
                    val label = HyperlinkLabel(news[0].title, UIUtil.getLabelForeground(), UIUtil.getLabelBackground(), UIUtil.getLabelForeground())
                    label.font = JBUI.Fonts.smallFont()
                    label.addHyperlinkListener {
                        val url = news[0].url
                        if (!url.isNullOrBlank()) {
                            BrowserUtil.browse(url.trim())
                        }
                    }
                    itemPanel.add(label)
                    itemPanel.background = background
                    news.forEach { item ->
                        val siteLabel = HyperlinkLabel(item.siteName)
                        siteLabel.addHyperlinkListener {
                            val url = item.url
                            if (!url.isNullOrBlank()) {
                                BrowserUtil.browse(url.trim())
                            }
                        }
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