package com.madfish.ide.model

/**
 * Created by Roger™
 */
class RHNews(
        var authorName: String = "",
        var language: String = "zh-cn",
        var siteName: String = "",
        var siteSlug: String = "",
        var summary: String = "",
        var summaryAuto: String = "",
        var url: String = "",
        var mobileUrl: String = ""
) : RHBaseItem() {

    override fun getSummaryText() = summary

    override fun getSearchTextList() = setOf(title, authorName, summary, siteName)

    override fun getUrlText() = url
}