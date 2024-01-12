package com.vikas.kmm

import com.vikas.kmm.data.Database
import com.vikas.kmm.db.Hello
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.ContentType.Application.Json
import kotlinx.serialization.json.Json

class AppDao(databaseDriverFactory: DatabaseDriverFactory) {
    var database = Database(databaseDriverFactory)
    private val platform: Platform = getPlatform()

    private val httpClient = httpClient {
        install(Logging) {
            level = LogLevel.HEADERS
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(tag = "HTTP Client", message = message)
                } } }
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
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
}