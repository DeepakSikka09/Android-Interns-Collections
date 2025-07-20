package com.example.ktorapplication

import kotlinx.serialization.Serializable

@Serializable
data class SuccessData(
    val name : String = "",
    val salary: String = "",
    val age : String = "",
    val id: Int = 0
)
