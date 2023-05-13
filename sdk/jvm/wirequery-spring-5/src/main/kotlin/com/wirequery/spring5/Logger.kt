package com.wirequery.spring5

interface Logger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
    fun debug(message: String)
}
