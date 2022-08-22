package com.madfish.ide.view

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.madfish.ide.model.RHInstantView
import com.madfish.ide.util.RHUtil
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.Action
import javax.swing.JPanel
import javax.swing.text.html.HTMLEditorKit

/**
 * Created by Roger™
 */
class InstantViewDialog(project: Project?, item: RHInstantView) : DialogWrapper(project) {

    private var rootPanel = JPanel()

    init {
        title = item.title
        myPreferredFocusedComponent = rootPanel

        myOKAction = object : OkAction() {
            override fun doAction(e: ActionEvent?) {
                if (item.url.isNotBlank()) {
                    BrowserUtil.browse(item.url.trim())
                }
                super.doAction(e)
            }
        }
        myOKAction.putValue(Action.SMALL_ICON, RHIcons.CHROME)
        myOKAction.putValue(Action.NAME, RHUtil.message("RHInstantViewAction.dialog.viewBtn"))

        val panel = HtmlPanel(true)
        val kit = HTMLEditorKit()
        panel.editorKit = kit
        val style = kit.styleSheet
        style.addRule("html body { font-size: 12px; font-family: PingFang SC, Tahoma, Helvetica, Arial, Hiragino Sans GB, Microsoft YaHei, Heiti SC, WenQuanYi Micro Hei, sans-serif; }")
        style.addRule("html body { width: 600px; margin: 0 auto; padding: 10px }")
        style.addRule("img { max-width: 90%; max-height: 200px; margin: 0 auto; }")

        val doc = kit.createDefaultDocument()
        panel.document = doc
        panel.text = Jsoup.clean(item.content, Whitelist.basic()
                .addTags("img")
                .addAttributes("img", "src")
                .addProtocols("img", "src", "http", "https"))

        val scrollPane = JBScrollPane(panel)
        scrollPane.preferredSize = Dimension(640, 400)
        scrollPane.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                panel.setSize(panel.width - 20, panel.height - 20)
            }
        })
        rootPanel.add(scrollPane)
        isResizable = false
        init()
    }

    override fun createCenterPanel() = rootPanel

    override fun createActions() = arrayOf(myOKAction)
}