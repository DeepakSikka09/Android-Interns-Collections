package com.example.ktorapplication

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val name: String,
    val salary: String,
    val age: String
)
