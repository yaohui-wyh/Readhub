package com.madfish.ide.model

import com.google.gson.reflect.TypeToken
import com.madfish.ide.util.RHUtil
import java.lang.reflect.Type

/**
 * Created by Roger™
 */
enum class RHCategory(
        var nameKey: String = "",
        var path: String = "",
        var apiPath: String = ""
) {
    TOPIC("RHCategory.topic", "", "topic"),
    NEWS("RHCategory.news", "news", "news"),
    TECH_NEWS("RHCategory.technews", "tech", "technews"),
    BLOCKCHAIN("RHCategory.blockchain", "blockchain", "blockchain"),
    JOB("RHCategory.jobs", "jobs", "jobs");

    fun getApiResType(): Type {
        return when {
            this == TOPIC -> object : TypeToken<RHApiResponse<RHTopic>>() {}.type
            this == JOB -> object : TypeToken<RHApiResponse<RHJob>>() {}.type
            else -> object : TypeToken<RHApiResponse<RHNews>>() {}.type
        }
    }

    fun getName() = RHUtil.message(this.nameKey)
}