package com.madfish.ide.view

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SideBorder
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.model.RHJob
import com.madfish.ide.util.RHUtil
import java.awt.Cursor
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

/**
 * Created by Roger™
 */
class RHJobWindowContent(project: Project) : RHToolWindowContent(project, RHCategory.JOB) {

    override fun getColumns(): Array<ColumnInfo<RHBaseItem, *>> {
        val titleColumn = object : ColumnInfo<RHBaseItem, String>("职位") {
            override fun valueOf(item: RHBaseItem?) = item?.getTitleText().orEmpty()
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHBaseCellRenderer(item)
            override fun getWidth(table: JTable?) = JBUI.scale(120)
        }

        val countColumn = object : ColumnInfo<RHBaseItem, String>("职位总数") {
            override fun valueOf(item: RHBaseItem?): String? {
                if (item is RHJob) {
                    return item.jobCount.toString()
                }
                return ""
            }

            override fun getWidth(table: JTable?) = JBUI.scale(80)
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHBaseCellRenderer(item, false)
        }

        val cityColumn = object : ColumnInfo<RHBaseItem, String>("城市分布") {
            override fun valueOf(item: RHBaseItem?): String? {
                if (item is RHJob) {
                    return item.cities.map { "${it.key} (${it.value})" }.joinToString(", ")
                }
                return ""
            }

            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHBaseCellRenderer(item, false)
        }

        val expColumn = object : ColumnInfo<RHBaseItem, String>("工作经验") {
            override fun valueOf(item: RHBaseItem?): String? {
                if (item is RHJob) {
                    return "${item.experienceLower}-${item.experienceUpper}年"
                }
                return ""
            }

            override fun getWidth(table: JTable?) = JBUI.scale(80)
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHBaseCellRenderer(item, false)
        }

        val salaryColumn = object : ColumnInfo<RHBaseItem, String>("薪资区间") {
            override fun valueOf(item: RHBaseItem?): String? {
                if (item is RHJob) {
                    return "${item.salaryLower}-${item.salaryUpper}k"
                }
                return ""
            }

            override fun getWidth(table: JTable?) = JBUI.scale(90)
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHBaseCellRenderer(item, false)
        }

        val datetimeColumn = object : ColumnInfo<RHBaseItem, String>("更新时间") {
            override fun valueOf(item: RHBaseItem?) = RHUtil.getTimeDelta(item?.getDateTime())
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHSmallCellRenderer()
            override fun getWidth(table: JTable?) = JBUI.scale(80)
        }
        return arrayOf(titleColumn, countColumn, cityColumn, expColumn, salaryColumn, datetimeColumn)
    }

    override fun setLinkContent(parent: JPanel, item: RHBaseItem?) {
        if (item is RHJob) {
            var max = item.jobsArray.size
            if (max > 10) max = 10
            item.jobsArray.subList(0, max).forEach { jobItem ->
                val container = JPanel()
                container.layout = BoxLayout(container, BoxLayout.Y_AXIS)
                container.border = IdeBorderFactory.createTitledBorder(jobItem.title, true)
                container.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

                val panel1 = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
                panel1.add(JBLabel(jobItem.company, UIUtil.ComponentStyle.SMALL))
                val salaryLabel = HyperlinkLabel()
                panel1.add(salaryLabel)
                salaryLabel.setHtmlText("<b>${jobItem.salaryLower}-${jobItem.salaryUpper}k</b> ")
                salaryLabel.font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)

                val panel2 = JPanel(FlowLayout(FlowLayout.LEFT, 0, 2))
                panel2.add(JBLabel(jobItem.city, UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER))
                panel2.add(JBLabel(" ${jobItem.experienceLower}-${jobItem.experienceUpper}年 ", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER))
                panel2.add(JBLabel("     [${jobItem.siteName}]", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER))

                container.add(panel1)
                container.add(panel2)
                container.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        if (jobItem.url.isNotBlank()) {
                            BrowserUtil.browse(jobItem.url.trim())
                        }
                    }
                })
                parent.add(container)
            }
        }
    }

    override fun buildDetailComponent(): JComponent {
        myLinkPanel.background = background
        return ScrollPaneFactory.createScrollPane(myLinkPanel, SideBorder.TOP)
    }
}