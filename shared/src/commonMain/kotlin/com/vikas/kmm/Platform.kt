package com.vikas.kmm

import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient

expect fun initLogger()