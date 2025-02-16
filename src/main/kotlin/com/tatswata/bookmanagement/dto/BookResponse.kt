package com.tatswata.bookmanagement.dto

data class BookResponse(
    val id: Int,
    val title: String,
    val price: Int,
    val status: String
)