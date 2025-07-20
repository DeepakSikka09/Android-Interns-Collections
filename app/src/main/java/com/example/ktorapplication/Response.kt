package com.example.ktorapplication

sealed class Response{
    object Empty:Response()
    class Success(
        val data:Data
    ):Response()

    class Error(
        val msg:String
    ):Response()

    object Loading:Response()
}
