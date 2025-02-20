package com.tatswata.bookmanagement.exception

data class ErrorResponse(
    val errorCode: String,
    val errorMessage: String
)