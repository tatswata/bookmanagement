package com.tatswata.bookmanagement.domain.book

import com.tatswata.bookmanagement.domain.author.AuthorId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BookTests {

    @Test
    fun 有効なデータで本を作成できる() {
        // When
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // Then
        assertEquals("Effective Java", book.title.value)
        assertEquals(4500, book.price.value)
        assertEquals(BookStatus.UNPUBLISHED, book.status)
        assertEquals(1, book.authorIds.size)
    }

    @Test
    fun 空のタイトルでは本を作成できない() {
        // When
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle(""), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        // Then
        assertEquals("Title cannot be empty", exception.message)
    }

    @Test
    fun タイトルが最大長ちょうどの本を作成できる() {
        // Given
        val longTitle = "a".repeat(500)

        // When
        val book = Book(BookId(1), BookTitle(longTitle), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // Then
        assertEquals(500, book.title.value.length)
    }

    @Test
    fun タイトルが最大長を超える本は作成できない() {
        // Given
        val longTitle = "a".repeat(501)

        // When
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle(longTitle), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        // Then
        assertEquals("Title cannot exceed 500 characters", exception.message)
    }

    @Test
    fun 価格が0の本を作成できる() {
        // When
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(0), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // Then
        assertEquals(0, book.price.value)
    }

    @Test
    fun 負の価格では本を作成できない() {
        // When
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle("Effective Java"), BookPrice(-1), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        // Then
        assertEquals("Price cannot be negative", exception.message)
    }

    @Test
    fun 価格が最大値ちょうどの本を作成できる() {
        // When
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(1_000_000), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // Then
        assertEquals(1_000_000, book.price.value)
    }

    @Test
    fun 価格が最大値を超える本は作成できない() {
        // When
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle("Effective Java"), BookPrice(1_000_001), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        }

        // Then
        assertEquals("Price cannot exceed 1,000,000", exception.message)
    }

    @Test
    fun 著者がいない本は作成できない() {
        // When
        val exception = assertThrows<IllegalArgumentException> {
            Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, emptyList())
        }

        // Then
        assertEquals("A book must have at least one author", exception.message)
    }

    @Test
    fun 本のタイトルを更新できる() {
        // Given
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // When
        book.updateTitle(BookTitle("Clean Code"))

        // Then
        assertEquals("Clean Code", book.title.value)
    }

    @Test
    fun 本の価格を更新できる() {
        // Given
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // When
        book.updatePrice(BookPrice(5000))

        // Then
        assertEquals(5000, book.price.value)
    }

    @Test
    fun 本の著者を更新できる() {
        // Given
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // When
        book.updateAuthors(listOf(AuthorId(2), AuthorId(3)))

        // Then
        assertEquals(2, book.authorIds.size)
        assertEquals(2, book.authorIds[0].value)
        assertEquals(3, book.authorIds[1].value)
    }

    @Test
    fun 本を出版済みに変更できる() {
        // Given
        val book = Book(BookId(1), BookTitle("Effective Java"), BookPrice(4500), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))

        // When
        book.publish()

        // Then
        assertEquals(BookStatus.PUBLISHED, book.status)
    }
}