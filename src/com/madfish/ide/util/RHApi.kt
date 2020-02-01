package com.madfish.ide.util

import com.intellij.openapi.diagnostic.Logger
import com.madfish.ide.configurable.RHData
import com.madfish.ide.internal.d
import com.madfish.ide.model.RHApiResponse
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.model.RHInstantView
import com.madfish.ide.util.RHUtil.Companion.gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by Roger™
 */
class RHApi {

    companion object {
        private val logger = Logger.getInstance(this::class.java)
        var httpClient = OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

        init {
            // Disable SSL Certificate check
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
                })
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                httpClient = OkHttpClient
                        .Builder()
                        .sslSocketFactory(sslContext.socketFactory, trustAllCerts.first() as X509TrustManager)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build()
            } catch (e: Exception) {
                logger.d("Disable SSL Cert exception", e)
            }
        }

        fun refreshAll(): ApiResult<Boolean> {
            val apiSeq = sequence {
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
            val url = "${Constants.Readhub.apiHost}/${category.apiPath}?lastCursor=$cursor&pageSize=$pageSize"
            return try {
                val request = Request.Builder().url(url).header("Content-Type", "application/json; charset=UTF-8").build()
                val response = httpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    ApiResult(false, errcode = ErrMessage.API_NETWORK_ERROR)
                } else {
                    val res = response.body?.string().orEmpty()
                    val result = gson.fromJson<RHApiResponse<out RHBaseItem>?>(res, category.getApiResType())
                    result?.data?.forEach { it.category = category }
                    ApiResult(true, result = result)
                }
            } catch (e: Exception) {
                logger.d(ErrMessage.API_NETWORK_ERROR.text, e)
                ApiResult(false, ErrMessage.API_NETWORK_ERROR, e.message.orEmpty())
            }
        }

        fun getInstantView(topicId: String): ApiResult<RHInstantView> {
            val url = "${Constants.Readhub.apiHost}/topic/instantview?topicId=$topicId"
            return try {
                val request = Request.Builder().url(url).header("Content-Type", "application/json; charset=UTF-8").build()
                val response = httpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    ApiResult(false, errcode = ErrMessage.API_NETWORK_ERROR)
                } else {
                    val res = response.body?.string().orEmpty()
                    ApiResult(true, result = gson.fromJson<RHInstantView>(res, RHInstantView::class.java))
                }
            } catch (e: Exception) {
                logger.d(ErrMessage.API_NETWORK_ERROR.text, e)
                ApiResult(false, ErrMessage.API_NETWORK_ERROR, e.message.orEmpty())
            }
        }
    }
}