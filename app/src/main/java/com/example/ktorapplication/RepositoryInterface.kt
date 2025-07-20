package com.example.ktorapplication

interface RepositoryInterface {
    suspend fun createData(request: Request):Response
    suspend fun fetchData(): Response
}