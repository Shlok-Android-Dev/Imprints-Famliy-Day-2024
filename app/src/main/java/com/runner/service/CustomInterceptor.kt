package com.runner.service

import android.content.Context
import android.util.Log
import com.ruhe.utils.NetworkAlertUtility.isConnectingToInternet
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit

class CustomInterceptor(private val logger: Logger) : Interceptor {
    var version: String? = null
    var lang: String? = null
    var headers: Headers? = null

    @Volatile
    private var level = Level.NONE
    private var context: Context? = null

    constructor(context: Context?, lang: String?, version: String?) : this(Logger.DEFAULT) {
        setLevel(Level.BODY)
        this.context = context
        this.version = version
        this.lang = lang
    }

    /**
     * Change the level at which this interceptor logs.
     */
    fun setLevel(level: Level) {
        this.level = level
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = level
        val original = chain.request()
        val requestBuilder = original.newBuilder().addHeader("os", "android")
                .addHeader("version", version)
                .addHeader("language", lang)
        val request = requestBuilder.build()
        val response = chain.proceed(request)
        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val connection = chain.connection()
        val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
        var requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"
        }
        logger.log(requestStartMessage)
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody!!.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType())
                }
                if (requestBody.contentLength() != -1L) {
                    logger.log("Content-Length: " + requestBody.contentLength())
                }
            }
            val headers = request.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                    logger.log(name + ": " + headers.value(i))
                }
                i++
            }
            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method())
            } else if (bodyEncoded(request.headers())) {
                logger.log("--> END " + request.method() + " (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)
                var charset = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                logger.log("")
                logger.log(buffer.readString(charset))
                logger.log("--> END " + request.method()
                        + " (" + requestBody.contentLength() + "-byte body)")
            }
        }
        val startNs = System.nanoTime()
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (if (!logHeaders) ", "
                + bodySize + " body" else "") + ')')
        if (logHeaders) {
            val headers = response.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                logger.log(headers.name(i) + ": " + headers.value(i))
                i++
            }
            if (!logBody /*|| !HttpEngine.hasBody(response)*/) {
                logger.log("<-- END HTTP")
            } else if (bodyEncoded(response.headers())) {
                logger.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()
                var charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = try {
                        contentType.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        logger.log("")
                        logger.log("Couldn't decode the response body; charset is likely malformed.")
                        logger.log("<-- END HTTP")
                        return response
                    }
                }
                if (contentLength != 0L) {
                    logger.log("")
                    logger.log(buffer.clone().readString(charset))
                }
                logger.log("<-- END HTTP (" + buffer.size() + "-byte body)")
            }
        }
        return if (isConnectingToInternet(context)) {
            val maxAge = 60 // read from cache for 1 minute
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .build()
        } else {
            val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
        }

        // return response;
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }
    enum class Level {
        NONE, BASIC, HEADERS, BODY
    }

    interface Logger {
        fun log(message: String?)
        companion object {
            val DEFAULT: Logger = object : Logger {
                override fun log(message: String?) {
                    //Platform.get().logW(message);
                    Log.e("@@@@Response::", message+"@@Msg@@"!!)
                    //AppUtils.showRoughLog("Response",message);
                }
            }
        }
    }
    companion object {
        private val UTF8 = Charset.forName("UTF-8")
        private fun protocol(protocol: Protocol): String {
            return if (protocol == Protocol.HTTP_1_0) "HTTP/1.0" else "HTTP/1.1"
        }

        private fun requestPath(url: HttpUrl): String {
            val path = url.encodedPath()
            val query = url.encodedQuery()
            return if (query != null) "$path?$query" else path
        }
    }
}