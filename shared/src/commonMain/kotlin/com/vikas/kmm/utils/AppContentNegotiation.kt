package com.vikas.kmm.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentConverterException
import io.ktor.client.plugins.contentnegotiation.JsonContentTypeMatcher
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.accept
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.ContentTypeMatcher
import io.ktor.http.HttpHeaders
import io.ktor.http.charset
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.serialization.Configuration
import io.ktor.serialization.ContentConverter
import io.ktor.serialization.suitableCharset
import io.ktor.util.AttributeKey
import io.ktor.util.KtorDsl
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charsets

public class AppContentNegotiation internal constructor(
    internal val registrations: List<Config.ConverterRegistration>
) {

    /**
     * A [ContentNegotiation] configuration that is used during installation.
     */
    public class Config : Configuration {

        internal class ConverterRegistration(
            val converter: ContentConverter,
            val contentTypeToSend: ContentType,
            val contentTypeMatcher: ContentTypeMatcher,
        )

        internal val registrations = mutableListOf<ConverterRegistration>()

        /**
         * Registers a [contentType] to a specified [converter] with an optional [configuration] script for a converter.
         */
        public override fun <T : ContentConverter> register(
            contentType: ContentType,
            converter: T,
            configuration: T.() -> Unit
        ) {
            val matcher = when (contentType) {
                ContentType.Application.Json -> JsonContentTypeMatcher
                else -> defaultMatcher(contentType)
            }
            register(contentType, converter, matcher, configuration)
        }

        /**
         * Registers a [contentTypeToSend] and [contentTypeMatcher] to a specified [converter] with
         * an optional [configuration] script for a converter.
         */
        public fun <T : ContentConverter> register(
            contentTypeToSend: ContentType,
            converter: T,
            contentTypeMatcher: ContentTypeMatcher,
            configuration: T.() -> Unit
        ) {
            val registration = ConverterRegistration(
                converter.apply(configuration),
                contentTypeToSend,
                contentTypeMatcher
            )
            registrations.add(registration)
        }

        private fun defaultMatcher(pattern: ContentType): ContentTypeMatcher =
            object : ContentTypeMatcher {
                override fun contains(contentType: ContentType): Boolean =
                    contentType.match(pattern)
            }
    }

    /**
     * A companion object used to install a plugin.
     */
    @KtorDsl
    public companion object Plugin : HttpClientPlugin<Config, AppContentNegotiation> {
        public override val key: AttributeKey<AppContentNegotiation> =
            AttributeKey("ContentNegotiation")

        override fun prepare(block: Config.() -> Unit): AppContentNegotiation {
            val config = Config().apply(block)
            return AppContentNegotiation(config.registrations)
        }

        override fun install(plugin: AppContentNegotiation, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { payload ->
                val registrations = plugin.registrations
                registrations.forEach { context.accept(it.contentTypeToSend) }

                if (subject is OutgoingContent /*|| DefaultIgnoredTypes.any { it.isInstance(payload) }*/) {
                    return@intercept
                }
                val contentType = context.contentType() ?: return@intercept

                if (payload is Unit) {
                    context.headers.remove(HttpHeaders.ContentType)
                    proceedWith(EmptyContent)
                    return@intercept
                }

                val matchingRegistrations =
                    registrations.filter { it.contentTypeMatcher.contains(contentType) }
                        .takeIf { it.isNotEmpty() } ?: return@intercept
                if (context.bodyType == null) return@intercept
                context.headers.remove(HttpHeaders.ContentType)

                // Pick the first one that can convert the subject successfully
                val serializedContent = matchingRegistrations.firstNotNullOfOrNull { registration ->
                    registration.converter.serialize(
                        contentType,
                        contentType.charset() ?: Charsets.UTF_8,
                        context.bodyType!!,
                        payload
                    )
                } ?: throw ContentConverterException(
                    "Can't convert $payload with contentType $contentType using converters " +
                            matchingRegistrations.joinToString { it.converter.toString() }
                )

                proceedWith(serializedContent)
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
                if (body !is ByteReadChannel) return@intercept
                if (info.type == ByteReadChannel::class) return@intercept

                // !!!!!!! Provide desired content type here
                val contentType = context.response.contentType() ?: return@intercept

                val registrations = plugin.registrations
                val matchingRegistrations = registrations
                    .filter { it.contentTypeMatcher.contains(contentType) }
                    .takeIf { it.isNotEmpty() } ?: return@intercept

                // Pick the first one that can convert the subject successfully
                val parsedBody = matchingRegistrations.firstNotNullOfOrNull { registration ->
                    registration.converter
                        .deserialize(context.request.headers.suitableCharset(), info, body)
                } ?: return@intercept
                val response = HttpResponseContainer(info, parsedBody)
                proceedWith(response)
            }
        }
    }
}