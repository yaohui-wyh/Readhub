package com.madfish.ide.internal

import com.intellij.ide.BrowserUtil
import com.intellij.notification.NotificationListener
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.util.Consumer
import com.madfish.ide.util.Constants
import com.madfish.ide.util.IdeVersionUtil
import com.madfish.ide.util.Notification
import com.mashape.unirest.http.Unirest
import org.json.JSONObject
import java.awt.Component
import java.awt.datatransfer.StringSelection

/**
 * Created by Roger™
 */
class RHErrorReportSubmitter : ErrorReportSubmitter() {

    private val issueUrlKey = "html_url"
    private val logger = Logger.getInstance(this::class.java)

    override fun getReportActionText() = "报告 Readhub Bug"

    override fun submit(events: Array<out IdeaLoggingEvent>, additionalInfo: String?, parentComponent: Component, consumer: Consumer<SubmittedReportInfo>): Boolean {
        ProgressManager.getInstance().run(object : Task.Backgroundable(null, "正在提交问题...", true) {
            override fun run(indicator: ProgressIndicator) {
                val requestData = JSONObject()
                val msg = "IDE 版本: ${IdeVersionUtil.getIdeVersion()}\n\n" +
                        if (IdeVersionUtil.getPluginVersion().isNotEmpty()) "插件版本: ${IdeVersionUtil.getPluginVersion()}\n\n" else "" +
                        "堆栈信息:\n\n```\n\n${events[0].toString().trim()}\n\n```\n\n" +
                        if (!additionalInfo.isNullOrBlank()) "额外信息: $additionalInfo" else ""
                try {
                    requestData.put("body", msg)
                    requestData.put("title", "[BugReport] ${if (events[0].message.length > 120) {
                        events[0].message.substring(0, 120).trim()
                    } else {
                        events[0].message.toString().trim()
                    }}")
                    val response = Unirest.post(Constants.BUG_REPORTER_API_URL)
                            .header("Content-Type", "application/json; charset=UTF-8")
                            .body(requestData).asJson()
                    val jsonObject = response.body.`object`
                    if (jsonObject != null && jsonObject.optString(issueUrlKey).isNotEmpty()) {
                        Notification.successBalloon(null,
                                "问题已经上报至 <a href=\"#issue\">Github Issue</a>，感谢您的反馈！",
                                NotificationListener { _, _ -> BrowserUtil.browse(jsonObject.getString(issueUrlKey)) },
                                Constants.PLUGIN_NAME)
                    }
                } catch (ex: Exception) {
                    CopyPasteManager.getInstance().setContents(StringSelection(msg))
                    Notification.warnBalloon(null,
                            "问题已拷贝到系统剪贴板，请您前往 <a href=\"#issue\">Github Issue</a> 提交问题，感谢您的反馈！",
                            NotificationListener { _, _ -> BrowserUtil.browse(Constants.BUG_REPORTER_WEB_URL) },
                            Constants.PLUGIN_NAME)
                    logger.info(ex)
                }
            }
        })
        return true
    }
}