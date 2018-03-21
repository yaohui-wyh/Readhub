package com.madfish.ide.messages

import com.intellij.util.messages.Topic
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory

/**
 * Created by Roger™
 */
val READHUB_VIEW_TOPIC: Topic<TableViewListener> = Topic.create("READHUB_VIEW_TOPIC", TableViewListener::class.java)

interface TableViewListener {

    fun onItemClicked(name: String, obj: RHBaseItem?)

    fun updateTable(name: String)
}

val READHUB_REFRESH_TOPIC: Topic<RefreshListener> = Topic.create("READHUB_REFRESH_TOPIC", RefreshListener::class.java)

interface RefreshListener {

    fun refreshItems(category: RHCategory? = null, background: Boolean = true)

    fun loadPrevItems(category: RHCategory)

    fun clearItems()
}