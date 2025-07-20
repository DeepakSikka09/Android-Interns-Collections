package com.example.ktorapplication

import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.path
import javax.inject.Inject

class RepositoryImpl @Inject constructor(val kClient: HttpClient):RepositoryInterface {
    override suspend fun createData(request: Request): Response {
        return kClient.apiCall {
            url{
                path("create")//end point
                method = HttpMethod.Post
                setBody(request)
            }
        }.await()
    }

    override suspend fun fetchData(): Response {
        return kClient.apiCall {
            url{
                protocol = URLProtocol.HTTPS
                host = "dummyjson.com"
                path("products")//end point
                method = HttpMethod.Get
            }
        }.await()
    }
}