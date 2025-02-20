package com.tatswata.bookmanagement.domain.book

import com.tatswata.bookmanagement.domain.author.AuthorId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BookTests {

    @Test
    fun 有効なデータで本を作成できる() {
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        assertEquals("Effective Java", book.title.value)
        assertEquals(4500, book.price.value)
        assertEquals(BookStatus.UNPUBLISHED, book.status)
        assertEquals(1, book.authorIds.size)
    }

    @Test
    fun 空のタイトルでは本を作成できない() {
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle(""), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        assertEquals("Title cannot be empty", exception.message)
    }

    @Test
    fun タイトルが最大長を超える本は作成できない() {
        val longTitle = "a".repeat(501)

        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle(longTitle), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        assertEquals("Title cannot exceed 500 characters", exception.message)
    }

    @Test
    fun 負の価格では本を作成できない() {
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle("Effective Java"), BookPrice(-1), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        assertEquals("Price cannot be negative", exception.message)
    }

    @Test
    fun 価格が最大値を超える本は作成できない() {
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle("Effective Java"), BookPrice(1_000_001), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        assertEquals("Price cannot exceed 1,000,000", exception.message)
    }

    @Test
    fun 著者がいない本は作成できない() {
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, emptyList())
        }

        assertEquals("A book must have at least one author", exception.message)
    }

    @Test
    fun 本のタイトルを更新できる() {
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        book.updateTitle(BookTitle("Clean Code"))

        assertEquals("Clean Code", book.title.value)
    }

    @Test
    fun 本の価格を更新できる() {
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        book.updatePrice(BookPrice(5000))

        assertEquals(5000, book.price.value)
    }

    @Test
    fun 本の著者を更新できる() {
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        book.updateAuthors(listOf(AuthorId(2)))

        assertEquals(1, book.authorIds.size)
        assertEquals(2, book.authorIds[0].value)
    }

    @Test
    fun 本を出版済みに変更できる() {
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        book.publish()

        assertEquals(BookStatus.PUBLISHED, book.status)
    }
}