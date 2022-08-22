package com.madfish.ide.view

import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.table.IconTableCellRenderer
import com.madfish.ide.model.RHBaseItem
import java.awt.Component
import java.awt.Insets
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.SwingConstants

class RHBaseCellRenderer(
        private val item: RHBaseItem?,
        private val bold: Boolean = true
) : ColoredTableCellRenderer() {

    override fun customizeCellRenderer(table: JTable, value: Any?, selected: Boolean, hasFocus: Boolean, row: Int, column: Int) {
        ipad = Insets(0, 12, 0, 0)
        border = null
        if (item != null && item.finished) {
            append(value.toString(), SimpleTextAttributes.GRAYED_ATTRIBUTES)
        } else {
            append(value.toString(), if (bold) SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES else SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
    }
}

class RHSmallCellRenderer : ColoredTableCellRenderer() {

    override fun customizeCellRenderer(table: JTable, value: Any?, selected: Boolean, hasFocus: Boolean, row: Int, column: Int) {
        ipad = Insets(0, 0, 0, 12)
        border = null
        append(value.toString(), SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, UIUtil.getInactiveTextColor()), 30, SwingConstants.RIGHT)
    }
}

class RHIconCellRenderer : IconTableCellRenderer<Icon>() {
    override fun getIcon(value: Icon, table: JTable?, row: Int) = value

    override fun getTableCellRendererComponent(table: JTable, value: Any?, selected: Boolean, focus: Boolean, row: Int, column: Int): Component {
        val component = super.getTableCellRendererComponent(table, value, false, focus, row, column)
        component.background = if (selected) table.selectionBackground else table.background
        component.foreground = UIUtil.getInactiveTextColor()
        (component as JLabel).text = ""
        component.horizontalAlignment = SwingConstants.RIGHT
        component.border = JBUI.Borders.empty(0, 2, 0, 12)
        return component
    }
}