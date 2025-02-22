package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.domain.author.Author
import com.tatswata.bookmanagement.domain.author.AuthorBirthDate
import com.tatswata.bookmanagement.domain.author.AuthorId
import com.tatswata.bookmanagement.domain.author.AuthorName
import com.tatswata.bookmanagement.domain.book.Book
import com.tatswata.bookmanagement.domain.book.BookId
import com.tatswata.bookmanagement.domain.book.BookPrice
import com.tatswata.bookmanagement.domain.book.BookStatus
import com.tatswata.bookmanagement.domain.book.BookTitle
import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.repository.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class AuthorServiceTests {

    private val authorRepository: AuthorRepository = mock(AuthorRepository::class.java)
    private val bookRepository: BookRepository = mock(BookRepository::class.java)
    private val authorService = AuthorService(authorRepository, bookRepository)

    @Test
    fun 著者作成_正常系() {
        // Given
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        `when`(authorRepository.save(any<Author>())).thenReturn(author)

        // When
        val response = authorService.createAuthor("John Doe", LocalDate.parse("2000-01-01"))

        // Then
        verify(authorRepository, times(1)).save(any<Author>())
        assertEquals("John Doe", response.name)
        assertEquals("2000-01-01", response.birthDate)
    }

    @Test
    fun 著者更新_正常系() {
        // Given
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        `when`(authorRepository.findById(1)).thenReturn(author)
        `when`(authorRepository.save(any<Author>())).thenReturn(author)

        // When
        val response = authorService.updateAuthor(1, "Jane Doe", LocalDate.parse("1990-01-01"))

        // Then
        verify(authorRepository, times(1)).save(any<Author>())
        assertEquals("Jane Doe", response!!.name)
        assertEquals("1990-01-01", response.birthDate)
    }

    @Test
    fun 著者更新_存在しないidを指定した場合はIllegalArgumentExceptionを投げる() {
        // Given
        `when`(authorRepository.findById(1)).thenReturn(null)

        // When
        val exception = assertThrows<IllegalArgumentException> {
            authorService.updateAuthor(1, "Jane Doe", LocalDate.parse("1990-01-01"))
        }

        // Then
        assertEquals("Author with id 1 not found", exception.message)
    }

    @Test
    fun 著者に紐づく書籍取得() {
        // Given
        val books = listOf(
            Book(BookId(1), BookTitle("Effective Java"), BookPrice(100), BookStatus.PUBLISHED, listOf(AuthorId(1))),
            Book(BookId(2), BookTitle("Clean Code"), BookPrice(150), BookStatus.PUBLISHED, listOf(AuthorId(1)))
        )
        `when`(bookRepository.findBooksByAuthorId(1)).thenReturn(books)

        // When
        val response = authorService.getBooksWrittenByAuthor(1)

        // Then
        assertEquals(2, response.size)
        assertEquals("Effective Java", response[0].title)
        assertEquals("Clean Code", response[1].title)
    }
}