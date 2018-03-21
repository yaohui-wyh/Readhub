package com.madfish.ide.internal

/**
 * Created by Roger™
 */
object RHDebug {

    val isInternal: Boolean = System.getProperty("readhub.is.internal")?.toBoolean() ?: false

    val customUseAgent: String = System.getProperty("readhub.httpHeader.userAgent")?.toString().orEmpty()
}