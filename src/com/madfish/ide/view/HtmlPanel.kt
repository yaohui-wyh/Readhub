package com.madfish.ide.view

import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.BrowserHyperlinkListener
import com.intellij.util.ui.UIUtil
import java.io.IOException
import java.io.StringWriter
import javax.swing.JEditorPane
import javax.swing.text.BadLocationException

/**
 * Created by Roger™
 */
class HtmlPanel(private val scrollable: Boolean = false) : JEditorPane(UIUtil.HTML_MIME, "") {

    init {
        isEditable = false
        isOpaque = false
        putClientProperty(HONOR_DISPLAY_PROPERTIES, true)
        addHyperlinkListener { BrowserHyperlinkListener.INSTANCE.hyperlinkUpdate(it) }
    }

    override fun getScrollableTracksViewportWidth(): Boolean {
        if (scrollable) {
            return true
        }
        return super.getScrollableTracksViewportWidth()
    }

    override fun getSelectedText(): String {
        val doc = document
        val start = selectionStart
        val end = selectionEnd

        try {
            val p0 = doc.createPosition(start)
            val p1 = doc.createPosition(end)
            val sw = StringWriter(p1.offset - p0.offset)
            editorKit.write(sw, doc, p0.offset, p1.offset - p0.offset)
            return StringUtil.removeHtmlTags(sw.toString())
        } catch (ex: BadLocationException) {
            // IGNORE
        } catch (ex: IOException) {
            // IGNORE
        }
        return super.getSelectedText()
    }
}
