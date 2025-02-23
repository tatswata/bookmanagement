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
import org.mockito.kotlin.argumentCaptor
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class AuthorServiceTests {

    private val authorRepository: AuthorRepository = mock(AuthorRepository::class.java)
    private val bookRepository: BookRepository = mock(BookRepository::class.java)
    private val authorService = AuthorService(authorRepository, bookRepository)

    @Test
    fun createAuthor_正常系() {
        // Given
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        `when`(authorRepository.save(any<Author>())).thenReturn(author)

        // When
        val response = authorService.createAuthor("John Doe", LocalDate.parse("2000-01-01"))

        // Then
        verify(authorRepository, times(1)).save(any<Author>())
        assertEquals("John Doe", response.name)
        assertEquals("2000-01-01", response.birthDate)

        val authorCaptor = argumentCaptor<Author>()
        verify(authorRepository, times(1)).save(authorCaptor.capture())
        assertEquals(null, authorCaptor.firstValue.id)
        assertEquals(AuthorName("John Doe"), authorCaptor.firstValue.name)
        assertEquals(AuthorBirthDate(LocalDate.parse("2000-01-01")), authorCaptor.firstValue.birthDate)
    }

    @Test
    fun updateAuthor_正常系() {
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

        val authorCaptor = argumentCaptor<Author>()
        verify(authorRepository, times(1)).save(authorCaptor.capture())
        assertEquals(1, authorCaptor.firstValue.id!!.value)
        assertEquals(AuthorName("Jane Doe"), authorCaptor.firstValue.name)
        assertEquals(AuthorBirthDate(LocalDate.parse("1990-01-01")), authorCaptor.firstValue.birthDate)
    }

    @Test
    fun updateAuthor_変更後の値を渡さなかった属性はそのままになる() {
        // Given
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        `when`(authorRepository.findById(1)).thenReturn(author)
        `when`(authorRepository.save(any<Author>())).thenReturn(author)

        // When
        val response = authorService.updateAuthor(1, null, null)

        // Then
        verify(authorRepository, times(1)).save(any<Author>())
        assertEquals("John Doe", response!!.name)
        assertEquals("2000-01-01", response.birthDate)

        val authorCaptor = argumentCaptor<Author>()
        verify(authorRepository, times(1)).save(authorCaptor.capture())
        assertEquals(1, authorCaptor.firstValue.id!!.value)
        assertEquals(AuthorName("John Doe"), authorCaptor.firstValue.name)
        assertEquals(AuthorBirthDate(LocalDate.parse("2000-01-01")), authorCaptor.firstValue.birthDate)
    }

    @Test
    fun updateAuthor_存在しないidを指定した場合はIllegalArgumentExceptionを投げる() {
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
    fun getBooksWrittenByAuthor_正常系() {
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
