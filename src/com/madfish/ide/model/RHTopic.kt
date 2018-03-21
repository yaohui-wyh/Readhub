package com.madfish.ide.model

import java.time.LocalDateTime

/**
 * Created by Roger™
 */
class RHTopic(
        var createdAt: LocalDateTime? = null,
        var updatedAt: LocalDateTime? = null,
        var createdBy: String = "",
        var firstPublishBy: String = "",
        var summary: String = "",
        var newsArray: List<RHTopicNewsItem> = listOf(),
        var wechatArray: List<Any> = listOf(),
        var weiboArray: List<Any> = listOf()) : RHBaseItem() {

    override fun getSummaryText() = summary

    override fun getDateTime(): LocalDateTime? = createdAt

    override fun getUrlText() = newsArray.firstOrNull { !it.url.isNullOrBlank() }?.url.orEmpty()

    override fun getSearchTextList(): Set<String> {
        val s = mutableSetOf(title, summary)
        newsArray.forEach { s.addAll(listOf(it.authorName, it.siteName, it.title)) }
        return s
    }
}

class RHTopicNewsItem(
        var id: Long = 0L,
        var authorName: String = "",
        var duplicateId: Long = 1L,
        var groupId: Long = 1L,
        var mobileUrl: String = "",
        var publishDate: LocalDateTime? = null,
        var siteName: String = "",
        var title: String = "",
        var url: String? = "")
