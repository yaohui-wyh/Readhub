package com.madfish.ide.view

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.ListCellRendererWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.UIUtil
import com.madfish.ide.configurable.RHData
import com.madfish.ide.configurable.RHSettings
import com.madfish.ide.messages.READHUB_REFRESH_TOPIC
import com.madfish.ide.util.Constants
import com.madfish.ide.util.RHUtil
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.*
import javax.swing.*

/**
 * Created by Roger™
 */
class RHConfigComponent {

    val mainPanel = JPanel()
    private val slider = RefreshRangeSlider(RefreshRanges.values())
    private val comboBox = ComboBox<LanguageItem>(LanguageItem.values())
    private var statsLabel = setStatsLabel()

    init {
        val settingsPanel = JPanel()
        settingsPanel.layout = BoxLayout(settingsPanel, BoxLayout.Y_AXIS)
        settingsPanel.border = IdeBorderFactory.createTitledBorder(RHUtil.message("Config.settings"), true)

        val languagePanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5))
        val languageLabel = JBLabel(RHUtil.message("Config.language"))
        languageLabel.border = IdeBorderFactory.createEmptyBorder(0, 0, 0, 15)
        languagePanel.add(languageLabel)
        languagePanel.add(comboBox)
        comboBox.selectedItem = RHSettings.instance.lang
        comboBox.renderer = object : ListCellRendererWrapper<LanguageItem>() {
            override fun customize(list: JList<*>?, item: LanguageItem?, index: Int, selected: Boolean, hasFocus: Boolean) {
                setText(RHUtil.message(item?.messageKey.orEmpty()))
            }
        }

        val refreshPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5))
        val refreshLabel = JBLabel(RHUtil.message("Config.autoRefresh"))
        refreshLabel.border = IdeBorderFactory.createEmptyBorder(0, 0, 0, 15)
        refreshPanel.add(refreshLabel)
        refreshPanel.add(slider)

        val statsPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5))
        statsPanel.add(statsLabel)
        setClearStatsLabel(statsPanel)

        settingsPanel.add(languagePanel)
        settingsPanel.add(refreshPanel)
        settingsPanel.add(statsPanel)

        val aboutPanel = JPanel()
        aboutPanel.layout = BoxLayout(aboutPanel, BoxLayout.Y_AXIS)
        aboutPanel.border = IdeBorderFactory.createTitledBorder(RHUtil.message("Config.about"), true)

        // ========= Info Panel ============
        val infoPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5))

        val authorLabel = HyperlinkLabel()
        val issueLabel = HyperlinkLabel()

        infoPanel.add(authorLabel)
        infoPanel.add(issueLabel)

        authorLabel.setHtmlText("${RHUtil.message("Config.connectAuthor")}: <a href=\"#author\">@Roger</a>，")
        authorLabel.setHyperlinkTarget(Constants.Readhub.authorUrl)

        issueLabel.setHtmlText("${RHUtil.message("Config.feedback")}: <a href=\"#feedback\">Github Issue</a>")
        issueLabel.setHyperlinkTarget(Constants.Readhub.bugReportUrl)

        // ========= Extra Panel ============
        val extraPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5))

        val label1 = JBLabel(RHUtil.message("Config.feedbackText1"), UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER)
        label1.border = IdeBorderFactory.createEmptyBorder(0, 0, 0, 8)
        extraPanel.add(label1)
        setRateLabel(extraPanel)

        val label2 = JBLabel(RHUtil.message("Config.feedbackText2"), UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER)
        label2.border = IdeBorderFactory.createEmptyBorder(0, 0, 0, 8)
        extraPanel.add(label2)
        setRewardLabel(extraPanel)

        aboutPanel.add(infoPanel)
        aboutPanel.add(extraPanel)

        mainPanel.layout = GridBagLayout()
        mainPanel.add(settingsPanel, GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        mainPanel.add(aboutPanel, GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
    }

    fun isModified(): Boolean {
        return slider.isModified() || comboBox.selectedItem != RHSettings.instance.lang
    }

    fun reset() {
        slider.setSelected(RHSettings.instance.refreshMode)
        comboBox.selectedItem = RHSettings.instance.lang
    }

    fun apply() {
        slider.getSelected()?.let {
            if (slider.isModified()) {
                RHSettings.instance.refreshMode = it
                RHSettings.instance.setRefreshTimer()
            }
        }
        RHSettings.instance.lang = comboBox.selectedItem as LanguageItem
    }

    private fun setStatsLabel(): JBLabel {
        val label = JBLabel(setStatsText(), UIUtil.ComponentStyle.SMALL).apply { setCopyable(true) }
        label.border = IdeBorderFactory.createEmptyBorder(0, 0, 0, 10)
        return label
    }

    private fun setStatsText(): String {
        val stats = RHData.instance.getReadStatistics()
        return "${RHUtil.message("Config.statistics")}: ${stats.joinToString(",   ") { "[${it.category.getName()}] ${it.readCount}" }}"
    }

    private fun setClearStatsLabel(parent: JPanel): HyperlinkLabel {
        val label = HyperlinkLabel()
        parent.add(label)

        label.setHtmlText("<a href=\"#clear\">${RHUtil.message("Config.clearStatistics")}</a>")
        label.setIcon(RHIcons.GC)
        label.font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)
        label.addHyperlinkListener {
            val response = Messages.showYesNoDialog(
                    RHUtil.message("Config.clearStatisticsMessage"),
                    RHUtil.message("Config.clearStatistics"),
                    RHUtil.message("Action.yes"),
                    RHUtil.message("Action.no"),
                    Messages.getQuestionIcon()
            )
            if (response == Messages.YES) {
                RHData.instance.clearCache()
                ApplicationManager.getApplication().messageBus.syncPublisher(READHUB_REFRESH_TOPIC).refreshItems()
                statsLabel.text = setStatsText()
            }
        }
        return label
    }

    private fun setRateLabel(parent: JPanel): HyperlinkLabel {
        val label = HyperlinkLabel()
        parent.add(label)

        label.setHtmlText(" <a href=\"#rate\">${RHUtil.message("Config.rate")}</a>")
        label.font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)
        label.setIcon(RHIcons.TOOLWINDOW_FAVORITES)
        label.setHyperlinkTarget(Constants.Readhub.rateUrl)
        return label
    }

    private fun setRewardLabel(parent: JPanel): HyperlinkLabel {
        val label = HyperlinkLabel()
        parent.add(label)

        label.setHtmlText("<a href=\"#reward\">${RHUtil.message("Config.reward")}</a>")
        label.font = UIUtil.getLabelFont(UIUtil.FontSize.SMALL)
        label.setIcon(RHIcons.COFFEE)
        label.addHyperlinkListener {
            RewardPopup.show(WindowManager.getInstance().suggestParentWindow(null))
        }
        return label
    }
}

class RefreshRangeSlider(private val ranges: Array<RefreshRanges>) : JSlider(
        SwingConstants.HORIZONTAL, 0, RefreshRanges.values().size - 1, 0) {

    init {
        minorTickSpacing = 1
        paintTicks = true
        paintTrack = true
        snapToTicks = true
        UIUtil.setSliderIsFilled(this, true)
        paintLabels = true
        val labels: Dictionary<Int, JBLabel> = Hashtable()
        ranges.forEach { labels.put(ranges.indexOf(it), JBLabel(RHUtil.message(it.messageKey), UIUtil.ComponentStyle.SMALL)) }
        labelTable = labels
    }

    fun setSelected(item: RefreshRanges) {
        value = ranges.indexOf(item)
    }

    fun getSelected() = ranges.find { value == ranges.indexOf(it) }

    fun isModified() = ranges[value] != RHSettings.instance.refreshMode
}

enum class RefreshRanges(val minutes: Int, val messageKey: String) {
    NONE(-1, "Config.refresh.none"),
    MINUTES_10(10, "Config.refresh.10m"),
    MINUTES_30(30, "Config.refresh.30m"),
    HOUR_1(60, "Config.refresh.1h"),
    HOUR_2(120, "Config.refresh.2h");
}

enum class LanguageItem(val locale: String, val messageKey: String) {
    CHINESE("zh", "Config.language.zh"),
    ENGLISH("en", "Config.language.en");
}