package com.madfish.ide.internal

import com.intellij.openapi.diagnostic.Logger

/**
 * Created by Roger™
 */
object RHDebug {

    val isInternal: Boolean = System.getProperty("readhub.internal")?.toBoolean() ?: false
}

fun Logger.d(message: String, t: Throwable? = null) {
    if (RHDebug.isInternal) {
        this.warn("[Readhub] $message", t)
    }
}