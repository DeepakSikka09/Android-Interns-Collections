package com.example.ktorapplication

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

var coroutineScope = CoroutineScope(Dispatchers.IO)
val ktorClient = HttpClient(Android) {
    engine {
        connectTimeout = 60000
        socketTimeout = 40000
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.e("Logger response->", message)
            }
        }
        level = LogLevel.BODY
    }

    install(ResponseObserver) {
        onResponse {
            Log.d("HTTP Status:", "${it.status.value}")
        }
    }

    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                val error = when (response.status) {
                    HttpStatusCode.Forbidden -> {
                        "Site is forbidden"
                    }

                    HttpStatusCode.NotFound -> {
                        "Site not found"
                    }

                    HttpStatusCode.BadGateway -> {
                        "Bad gateway"
                    }

                    HttpStatusCode.InternalServerError -> {
                        "internal server error"
                    }

                    HttpStatusCode.RequestTimeout -> {
                        "Request time out"
                    }

                    HttpStatusCode.Unauthorized -> {
                        "Unauthorized access"
                    }

                    HttpStatusCode.PaymentRequired -> {
                        "Payment Required"
                    }

                    HttpStatusCode.TooManyRequests -> {
                        "Too Many Request"
                    }

                    else -> {
                        "Network Error"
                    }
                }
                throw Exception(error)
            }
        }
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = "dummy.restapiexample.com/api/v1"
        }
        header("token", runBlocking {  getToken().await() })
        header("Content-Type", "application/json")
    }
    install(ContentNegotiation) {
        json(
            contentType = ContentType.Application.Json
        )

    }
}
fun getToken()= coroutineScope.async{
        "fdas"
}

inline fun HttpClient.apiCall(
    crossinline block: HttpRequestBuilder.() -> Unit
): Deferred<Response> = async{
     try {
        val response = request { block() }
        Response.Success(response.body())
    } catch (ex: Exception) {
        if (ex.message!!.contains("Unexpected JSON token")){
             Response.Error("Invalid Response")
        }else{
            Response.Error(ex.message!!)
        }
    }
}