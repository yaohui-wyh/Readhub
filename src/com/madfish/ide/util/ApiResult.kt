package com.madfish.ide.util

/**
 * Created by Roger™
 */
class ApiResult<T>(
        var success: Boolean = false,
        var errcode: ErrMessage? = null,
        var errorMessage: String = "",
        var result: T? = null
) {
    constructor(errResult: ApiResult<*>) : this(false, errResult.errcode, errResult.errorMessage)
}

enum class ErrMessage(var text: String) {
    API_NETWORK_ERROR(RHUtil.message("ErrMessage.API_NETWORK_ERROR"))
}