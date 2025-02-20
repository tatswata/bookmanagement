package com.tatswata.bookmanagement.exception

data class ErrorResponse(
    val status: String,
    val errorMessage: String
)