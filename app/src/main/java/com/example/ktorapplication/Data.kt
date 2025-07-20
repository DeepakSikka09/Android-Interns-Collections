package com.example.ktorapplication

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val status: String ="",
    val data: SuccessData = SuccessData(),
    val message: String = "",
    val products: List<Product> = listOf(),
    val total: Int = 0,
    val skip: Int =0,
    val limit: Int = 0
)
