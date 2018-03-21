package com.madfish.ide.model

import java.time.LocalDateTime

/**
 * Created by Roger™
 */
class RHJob(
        var uuid: String = "",
        var jobTitle: String = "",
        var experienceLower: Int = 0,
        var experienceUpper: Int = 0,
        var salaryLower: Int = 0,
        var salaryUpper: Int = 0,
        var companyCount: Int = 0,
        var jobCount: Int = 0,
        var cities: Map<String, Int> = mapOf(),
        var createdAt: LocalDateTime? = null,
        var sources: Map<String, Int> = mapOf(),
        var jobsArray: List<JobItem> = listOf()
) : RHBaseItem() {

    override fun getTitleText(): String = jobTitle

    override fun getSearchTextList(): Set<String> {
        val s = mutableSetOf(uuid, jobTitle)
        s.addAll(cities.keys)
        s.addAll(sources.keys)
        jobsArray.forEach { s.addAll(listOf(it.id, it.title, it.city, it.company, it.siteName)) }
        return s
    }
}

class JobItem(
        var id: String = "",
        var title: String = "",
        var city: String = "",
        var company: String = "",
        var experienceLower: Int = 0,
        var experienceUpper: Int = 0,
        var salaryLower: Int = 0,
        var salaryUpper: Int = 0,
        var siteName: String = "",
        var sponsor: Boolean = false,
        var url: String = ""
)