package com.madfish.ide.view

import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.UIUtil
import com.madfish.ide.model.RHBaseItem
import java.awt.Insets
import javax.swing.JTable
import javax.swing.SwingConstants

class RHBaseCellRenderer(private val item: RHBaseItem?, val bold: Boolean = true) : ColoredTableCellRenderer() {

    override fun customizeCellRenderer(table: JTable?, value: Any?, selected: Boolean, hasFocus: Boolean, row: Int, column: Int) {
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

    override fun customizeCellRenderer(table: JTable?, value: Any?, selected: Boolean, hasFocus: Boolean, row: Int, column: Int) {
        ipad = Insets(0, 0, 0, 12)
        border = null
        append(value.toString(), SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, UIUtil.getInactiveTextColor()), 30, SwingConstants.RIGHT)
    }
}
