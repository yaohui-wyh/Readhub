package com.madfish.ide.model

import java.time.LocalDateTime

/**
 * Created by Roger™
 */
open class RHBaseItem(
        var id: String = "",
        var order: Long = 0L,
        var title: String = "",
        var category: RHCategory = RHCategory.NEWS,
        var publishDate: LocalDateTime? = null,
        var finished: Boolean = false
) : Comparable<RHBaseItem> {

    open fun toCacheItem() = RHBaseItem(
            id = id,
            order = order,
            category = category,
            finished = finished
    )

    open fun getTitleText() = title

    open fun getSummaryText() = ""

    open fun getUrlText() = ""

    fun containsText(query: String): Boolean {
        if (query.isNotBlank()) {
            return getSearchTextList().any { it.contains(query, true) }
        }
        return true
    }

    open fun getSearchTextList(): Set<String> = setOf()

    open fun getDateTime(): LocalDateTime? = publishDate

    override fun compareTo(other: RHBaseItem): Int {
        return when (this.category) {
            RHCategory.TOPIC -> compareValuesBy(this, other, RHBaseItem::order)
            else -> compareValuesBy(this, other) { it.getDateTime() }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as RHBaseItem
        if (id != other.id) return false
        if (category != other.category) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + order.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }
}