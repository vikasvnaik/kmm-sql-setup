package com.vikas.kmm

import com.vikas.kmm.data.Database
import com.vikas.kmm.db.Hello
import com.vikas.kmm.utils.AppContentNegotiation
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AppDao(databaseDriverFactory: DatabaseDriverFactory) {
    var database = Database(databaseDriverFactory)
    private val platform: Platform = getPlatform()

    private val httpClient = httpClient {
        install(Logging) {
            level = LogLevel.HEADERS
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v("HTTP call : $message")
                } } }
        install(AppContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
            addDefaultResponseValidation()
        } }.also { initLogger() }

    fun insertData() {
        database.insert("hello")
        database.insert("hello1")
    }

    fun getData():List<Hello> {
        return database.getData()
    }
    fun greet(): String {
        return "Hello, ${platform.name}!"
    }

    suspend fun getRemoteDta(): HttpResponse {
        return httpClient.get("https://dummy.restapiexample.com/api/v1/employees")

    }
}