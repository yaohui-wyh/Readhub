package com.madfish.ide.configurable

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.MapAnnotation
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory

/**
 * Created by Roger™
 */
@State(name = "readhubData", storages = [(Storage("readhub/readhub-data.xml"))])
class RHData : PersistentStateComponent<RHData.State> {
    private var myItems: MutableMap<RHCategory, MutableList<RHBaseItem>> = mutableMapOf()
    private var myState = State()

    companion object {
        private const val maxItemSize = 2000
        val instance: RHData
            get() = ServiceManager.getService(RHData::class.java)
    }

    override fun getState() = myState

    override fun loadState(state: State) {
        myState = state
    }

    fun getItems(category: RHCategory, query: String = ""): List<RHBaseItem> {
        return myItems[category]?.filter { it.containsText(query) }.orEmpty()
    }

    fun getReadStatistics(): List<RHReadStatistics> {
        return RHCategory.values().map { c ->
            RHReadStatistics(c, myState.items[c]?.filter { it.finished }?.size ?: 0)
        }
    }

    @Synchronized
    fun setItemAsRead(item: RHBaseItem) {
        myItems[item.category]?.find { it.id == item.id }?.finished = true
        myState.items[item.category]?.find { it.id == item.id }?.finished = true
    }

    @Synchronized
    fun appendItems(category: RHCategory, newItems: List<RHBaseItem>) {
        val cacheList = myState.items[category].orEmpty()
        myState.items[category] = cacheList.union(newItems.map { it.toCacheItem() }).sortedDescending().toMutableList()

        val readList = cacheList.filter { it.finished }
        myItems[category] = myItems[category].orEmpty().union(newItems)
                .map { if (readList.contains(it)) it.finished = true; it }
                .sortedDescending().toMutableList()
    }

    @Synchronized
    fun clearCache() {
        myItems = mutableMapOf()
        myState.items = mutableMapOf()
    }

    // Call only once, before myItems set
    @Synchronized
    fun reduceCachedItems() {
        myState.items.forEach { (category, items) ->
            myState.items[category] = items.take(maxItemSize).toMutableList()
        }
    }

    class State {
        @MapAnnotation
        var items: MutableMap<RHCategory, MutableList<RHBaseItem>> = mutableMapOf()
    }
}

data class RHReadStatistics(val category: RHCategory, val readCount: Int)