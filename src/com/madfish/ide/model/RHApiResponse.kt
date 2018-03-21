package com.madfish.ide.model

/**
 * Created by Roger™
 */
class RHApiResponse<T>(
        var pageSize: Int = 10,
        var totalItems: Int = 0,
        var totalPages: Int = 0,
        var data: List<T> = listOf())
