package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.domain.author.Author
import com.tatswata.bookmanagement.domain.author.AuthorBirthDate
import com.tatswata.bookmanagement.domain.author.AuthorId
import com.tatswata.bookmanagement.domain.author.AuthorName
import com.tatswata.bookmanagement.domain.book.Book
import com.tatswata.bookmanagement.domain.book.BookId
import com.tatswata.bookmanagement.domain.book.BookPrice
import com.tatswata.bookmanagement.domain.book.BookStatus
import com.tatswata.bookmanagement.domain.book.BookTitle
import com.tatswata.bookmanagement.dto.AuthorResponse
import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.service.AuthorService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import java.time.LocalDate

class AuthorControllerTests {

    private val authorService = mock(AuthorService::class.java)
    private val authorController = AuthorController(authorService)

    @Test
    fun 著者作成_正常系() {
        // Given
        val createAuthorRequest = CreateAuthorRequest("John Doe", "2000-01-01")
        val createAuthor = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        val authorResponse = AuthorResponse(createAuthor)
        `when`(authorService.createAuthor("John Doe", LocalDate.parse("2000-01-01"))).thenReturn(authorResponse)

        // When
        val response = authorController.createAuthor(createAuthorRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(authorResponse, response.body)
    }

    @Test
    fun 著者作成_生年月日のフォーマットが不正な場合IllegalArgumentExceptionを投げる() {
        // Given
        val createAuthorRequest = CreateAuthorRequest("John Doe", "invalid-date")

        // When
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authorController.createAuthor(createAuthorRequest)
        }

        // Then
        assertEquals("Invalid format for birthDate", exception.message)
    }

    @Test
    fun 著者更新_正常系() {
        // Given
        val updateAuthorRequest = UpdateAuthorRequest("John Doe Jr", "2000-01-01")
        val updatedAuthor = Author(AuthorId(1), AuthorName("John Doe Jr"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        val authorResponse = AuthorResponse(updatedAuthor)
        `when`(authorService.updateAuthor(1, "John Doe Jr", LocalDate.parse("2000-01-01"))).thenReturn(authorResponse)

        // When
        val response = authorController.updateAuthor(1, updateAuthorRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(authorResponse, response.body)
    }

    @Test
    fun 著者更新_正常系_任意パラメータ無し() {
        // Given
        val updateAuthorRequest = UpdateAuthorRequest(null, null)
        val updatedAuthor = Author(AuthorId(1), AuthorName("John Doe Jr"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        val authorResponse = AuthorResponse(updatedAuthor)
        `when`(authorService.updateAuthor(1, null, null)).thenReturn(authorResponse)

        // When
        val response = authorController.updateAuthor(1, updateAuthorRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(authorResponse, response.body)
    }

    @Test
    fun 著者更新_更新対象が存在しない場合NOT_FOUNDを返す() {
        // Given
        val updateAuthorRequest = UpdateAuthorRequest("John Doe Jr", "2000-01-01")
        `when`(authorService.updateAuthor(1, "John Doe Jr", LocalDate.parse("2000-01-01"))).thenReturn(null)

        // When
        val response = authorController.updateAuthor(1, updateAuthorRequest)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun 著者更新_生年月日のフォーマットが不正な場合IllegalArgumentExceptionを投げる() {
        // Given
        val updateAuthorRequest = UpdateAuthorRequest("John Doe Jr", "invalid-date")

        // When
        val exception = assertThrows(IllegalArgumentException::class.java) {
            authorController.updateAuthor(1, updateAuthorRequest)
        }

        // Then
        assertEquals("Invalid format for birthDate", exception.message)
    }

    @Test
    fun 著者に紐づく書籍一覧取得_正常系() {
        // Given
        val book1 = Book(BookId(1), BookTitle("Clean Architecture"), BookPrice(100), BookStatus.valueOf("PUBLISHED"), listOf(AuthorId(1), AuthorId(2)))
        val book2 = Book(BookId(1), BookTitle("Clean Coder"), BookPrice(200), BookStatus.valueOf("PUBLISHED"), listOf(AuthorId(2)))
        val books = listOf(BookResponse(book1), BookResponse(book2))
        `when`(authorService.getBooksWrittenByAuthor(2)).thenReturn(books)

        // When
        val response = authorController.getBooksWrittenByAuthor(2)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(books, response.body)
    }
}
