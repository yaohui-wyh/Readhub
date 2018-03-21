package com.madfish.ide.util

import com.intellij.openapi.diagnostic.Logger
import com.madfish.ide.configurable.RHData
import com.madfish.ide.model.RHApiResponse
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.internal.RHDebug
import com.madfish.ide.util.Constants.Companion.READHUB_API_HOST
import com.mashape.unirest.http.Unirest
import org.apache.http.HttpStatus
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.coroutines.experimental.buildSequence

/**
 * Created by Roger™
 */
class RHApi {

    companion object {
        private val logger = Logger.getInstance(this::class.java)
        private val gson = RHUtil.gson!!

        init {
            Unirest.setTimeouts(30000, 30000)
            if (RHDebug.customUseAgent.isNotBlank()) {
                Unirest.setDefaultHeader("User-Agent", RHDebug.customUseAgent)
            }
        }

        fun refreshAll(): ApiResult<Boolean> {
            val apiSeq = buildSequence {
                yieldAll(RHCategory.values().map { fetchLatestItems(it) })
            }
            return ApiResult(success = apiSeq.all { it.result == true })
        }

        private fun refreshItems(category: RHCategory, cursor: String = "@null", pageSize: Int = 20): ApiResult<Boolean> {
            val ret = getReadhubResponse(category, cursor, pageSize)
            if (ret.success) {
                ret.result?.data?.let { RHData.instance.appendItems(category, it) }
            } else {
                return ApiResult(errResult = ret)
            }
            return ApiResult(true, result = true)
        }

        fun fetchLatestItems(category: RHCategory, pageSize: Int = 20): ApiResult<Boolean> {
            val cursor = when (category) {
                RHCategory.JOB -> LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).times(1000).toString()
                else -> "@null"
            }
            return refreshItems(category, cursor, pageSize)
        }

        fun fetchPrevItems(category: RHCategory, pageSize: Int = 20): ApiResult<Boolean> {
            val items = RHData.instance.getItems(category)
            val lastItem = items.lastOrNull()
            val cursor = when (category) {
                RHCategory.TOPIC -> lastItem?.order?.toString() ?: "@null"
                else -> lastItem?.getDateTime()?.atZone(ZoneId.of("UTC"))?.toEpochSecond()?.times(1000)?.toString() ?: "@null"
            }
            return refreshItems(category, cursor, pageSize)
        }

        fun getReadhubResponse(category: RHCategory, cursor: String = "@null", pageSize: Int = 20): ApiResult<RHApiResponse<out RHBaseItem>> {
            val url = "$READHUB_API_HOST/${category.apiPath}?lastCursor=$cursor&pageSize=$pageSize"
            return try {
                val response = Unirest.get(url)
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .asJson()
                if (response.status != HttpStatus.SC_OK) {
                    ApiResult(false, errcode = ErrMessage.API_NETWORK_ERROR)
                } else {
                    val jsonObject = response.body.`object`
                    val result = gson.fromJson<RHApiResponse<out RHBaseItem>?>(jsonObject.toString(), category.getApiResType())
                    result?.data?.forEach { it.category = category }
                    ApiResult(true, result = result)
                }
            } catch (e: Exception) {
                logger.d(ErrMessage.API_NETWORK_ERROR.text, e)
                ApiResult(false, ErrMessage.API_NETWORK_ERROR, e.message.orEmpty())
            }
        }
    }
}