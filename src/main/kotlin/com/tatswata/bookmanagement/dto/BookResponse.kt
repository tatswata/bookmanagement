package com.tatswata.bookmanagement.dto

import com.tatswata.bookmanagement.domain.book.Book

data class BookResponse(
    val id: Int,
    val title: String,
    val price: Int,
    val status: String
) {
    constructor(book: Book) : this(
        id = book.id!!.id,
        title = book.title.title,
        price = book.price.price,
        status = book.status.name
    )
}
