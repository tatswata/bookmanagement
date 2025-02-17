package com.tatswata.bookmanagement.domain.book

import com.tatswata.bookmanagement.domain.author.AuthorId

class Book(
    val id: BookId?,
    title: BookTitle,
    price: BookPrice,
    authorIds: List<AuthorId>,
    status: BookStatus
) {
    var title: BookTitle = title
        private set
    var price: BookPrice = price
        private set
    var authorIds: List<AuthorId> = authorIds
        private set
    var status: BookStatus = status
        private set

    init {
        require(authorIds.isNotEmpty()) { "A book must have at least one author" }
    }

    fun updatePrice(price: Int) {
        this.price = BookPrice(price)
    }

    fun updateAuthors(authorIds: List<AuthorId>) {
        require(authorIds.isNotEmpty()) { "A book must have at least one author" }
        this.authorIds = authorIds
    }

    fun publish() {
        this.status = BookStatus.PUBLISHED
    }
}

@JvmInline
value class BookId(val id: String)

@JvmInline
value class BookTitle(val title: String) {
    init {
        require(title.isNotBlank()) { "Title cannot be empty" }
        require(title.length <= 255) { "Title cannot exceed 255 characters" }
    }
}

@JvmInline
value class BookPrice(val price: Int) {
    init {
        require(price >= 0) { "Price cannot be negative" }
        require(price <= 1_000_000) { "Price cannot exceed 1,000,000" }
    }
}

enum class BookStatus(val jp: String) {
    UNPUBLISHED("未出版"),
    PUBLISHED("出版済み")
}