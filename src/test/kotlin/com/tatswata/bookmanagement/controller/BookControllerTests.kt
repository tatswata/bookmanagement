package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.domain.author.AuthorId
import com.tatswata.bookmanagement.domain.book.Book
import com.tatswata.bookmanagement.domain.book.BookId
import com.tatswata.bookmanagement.domain.book.BookPrice
import com.tatswata.bookmanagement.domain.book.BookStatus
import com.tatswata.bookmanagement.domain.book.BookTitle
import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.service.BookService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus

class BookControllerTests {

    private val bookService = mock(BookService::class.java)
    private val bookController = BookController(bookService)

    @Test
    fun 書籍作成_正常系() {
        // Given
        val createBookRequest = CreateBookRequest("Effective Java", 100, "UNPUBLISHED", listOf(1, 2))
        val createdBook = Book(BookId(1), BookTitle("Effective Java"), BookPrice(100), BookStatus.valueOf("UNPUBLISHED"), listOf(
            AuthorId(1), AuthorId(2)))
        val bookResponse = BookResponse(createdBook)
        `when`(bookService.createBook("Effective Java", 100, "UNPUBLISHED", listOf(1, 2))).thenReturn(bookResponse)

        // When
        val response = bookController.createBook(createBookRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(bookResponse, response.body)
    }

    @Test
    fun 書籍更新_正常系() {
        // Given
        val updateBookRequest = UpdateBookRequest("Clean Code", 150, "PUBLISHED", listOf(3, 4))
        val updatedBook = Book(BookId(1), BookTitle("Clean Code"), BookPrice(100), BookStatus.valueOf("PUBLISHED"), listOf(
            AuthorId(1), AuthorId(2)))
        val bookResponse = BookResponse(updatedBook)
        `when`(bookService.updateBook(1, "Clean Code", 150, "PUBLISHED", listOf(3, 4))).thenReturn(bookResponse)

        // When
        val response = bookController.updateBook(1, updateBookRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(bookResponse, response.body)
    }

    @Test
    fun 書籍更新_更新対象が存在しない場合NOT_FOUNDを返す() {
        // Given
        val updateBookRequest = UpdateBookRequest("Clean Code", 150, "PUBLISHED", listOf(3, 4))
        `when`(bookService.updateBook(1, "Clean Code", 150, "PUBLISHED", listOf(3, 4))).thenReturn(null)

        // When
        val response = bookController.updateBook(1, updateBookRequest)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }
}