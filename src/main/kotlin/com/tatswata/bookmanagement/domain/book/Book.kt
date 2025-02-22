package com.tatswata.bookmanagement.domain.book

import com.tatswata.bookmanagement.domain.author.AuthorId

class Book(
    val id: BookId?,
    title: BookTitle,
    price: BookPrice,
    status: BookStatus,
    authorIds: List<AuthorId>,
) {
    var title: BookTitle = title
        private set
    var price: BookPrice = price
        private set
    var status: BookStatus = status
        private set
    var authorIds: List<AuthorId> = authorIds
        private set

    init {
        require(authorIds.isNotEmpty()) { "A book must have at least one author" }
    }

    fun updateTitle(newTitle: BookTitle) {
        this.title = newTitle
    }

    fun updatePrice(newPrice: BookPrice) {
        this.price = newPrice
    }

    fun updateAuthors(newAuthorIds: List<AuthorId>) {
        require(authorIds.isNotEmpty()) { "A book must have at least one author" }
        this.authorIds = newAuthorIds
    }

    fun publish() {
        this.status = BookStatus.PUBLISHED
    }
}

@JvmInline
value class BookId(val value: Int)

@JvmInline
value class BookTitle(val value: String) {
    init {
        require(value.isNotBlank()) { "Title cannot be empty" }
        // 書籍タイトルの現実的な上限値として設定
        // 世界一長い小説のタイトル 375文字を根拠として余裕を持って設定した
        require(value.length <= 500) { "Title cannot exceed 500 characters" }
    }
}

@JvmInline
value class BookPrice(val value: Int) {
    init {
        require(value >= 0) { "Price cannot be negative" }
        // 書籍価格の現実的な上限値として設定
        // Amazonの書籍価格の最大値 3万円程度を根拠として大幅に余裕を持って設定した
        require(value <= 1_000_000) { "Price cannot exceed 1,000,000" }
    }
}

enum class BookStatus(val jp: String) {
    UNPUBLISHED("未出版"),
    PUBLISHED("出版済み")
}