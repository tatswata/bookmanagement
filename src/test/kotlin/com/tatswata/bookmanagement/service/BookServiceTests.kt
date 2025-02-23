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
class BookServiceTests {

    private val authorRepository: AuthorRepository = mock(AuthorRepository::class.java)
    private val bookRepository: BookRepository = mock(BookRepository::class.java)
    private val bookService = BookService(authorRepository, bookRepository)

    @Test
    fun createBook_正常系() {
        // Given
        `when`(authorRepository.findById(1)).thenReturn(Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01"))))
        `when`(bookRepository.save(any<Book>())).thenReturn(Book(BookId(1), BookTitle("Effective Java"), BookPrice(100), BookStatus.PUBLISHED, listOf(AuthorId(1))))

        // When
        val response = bookService.createBook("Effective Java", 100, "PUBLISHED", listOf(1))

        // Then
        assertEquals("Effective Java", response.title)
        assertEquals(100, response.price)
        assertEquals("PUBLISHED", response.status)

        val bookCaptor = argumentCaptor<Book>()
        verify(bookRepository, times(1)).save(bookCaptor.capture())
        assertEquals(null, bookCaptor.firstValue.id)
        assertEquals(BookTitle("Effective Java"), bookCaptor.firstValue.title)
        assertEquals(BookPrice(100), bookCaptor.firstValue.price)
        assertEquals(BookStatus.PUBLISHED, bookCaptor.firstValue.status)
        assertEquals(listOf(AuthorId(1)), bookCaptor.firstValue.authorIds)
    }

    @Test
    fun createBook_存在しない著者に紐付けようとしたらIllegalArgumentExceptionを投げる() {
        // Given
        `when`(authorRepository.findById(1)).thenReturn(null)

        // When
        val exception = assertThrows<IllegalArgumentException> {
            bookService.createBook("Effective Java", 100, "PUBLISHED", listOf(1))
        }

        // Then
        assertEquals("Author with id 1 not found", exception.message)
    }

    @Test
    fun updateBook_正常系() {
        // Given
        val book = Book(BookId(1), BookTitle("Old Title"), BookPrice(100), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        `when`(bookRepository.findById(1)).thenReturn(book)
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        `when`(authorRepository.findById(1)).thenReturn(author)
        val savedBook = Book(BookId(1), BookTitle("Effective Java"), BookPrice(100), BookStatus.PUBLISHED, listOf(AuthorId(1)))
        `when`(bookRepository.save(any<Book>())).thenReturn(savedBook)

        // When
        val response = bookService.updateBook(1, "Effective Java", 100, "PUBLISHED", listOf(1))

        // Then
        verify(bookRepository, times(1)).save(any<Book>())
        assertEquals("Effective Java", response!!.title)
        assertEquals(100, response.price)
        assertEquals("PUBLISHED", response.status)

        val bookCaptor = argumentCaptor<Book>()
        verify(bookRepository, times(1)).save(bookCaptor.capture())
        assertEquals(BookId(1), bookCaptor.firstValue.id)
        assertEquals(BookTitle("Effective Java"), bookCaptor.firstValue.title)
        assertEquals(BookPrice(100), bookCaptor.firstValue.price)
        assertEquals(BookStatus.PUBLISHED, bookCaptor.firstValue.status)
        assertEquals(listOf(AuthorId(1)), bookCaptor.firstValue.authorIds)
    }

    @Test
    fun updateBook_変更後の値を渡さなかった属性はそのままになる() {
        // Given
        val oldBook = Book(BookId(1), BookTitle("Old Title"), BookPrice(100), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        `when`(bookRepository.findById(1)).thenReturn(oldBook)
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01")))
        `when`(authorRepository.findById(1)).thenReturn(author)
        `when`(bookRepository.save(any<Book>())).thenReturn(oldBook)

        // When
        val response = bookService.updateBook(1, null, null, null, null)

        // Then
        verify(bookRepository, times(1)).save(any<Book>())
        assertEquals("Old Title", response!!.title)
        assertEquals(100, response.price)
        assertEquals("UNPUBLISHED", response.status)

        val bookCaptor = argumentCaptor<Book>()
        verify(bookRepository, times(1)).save(bookCaptor.capture())
        assertEquals(BookId(1), bookCaptor.firstValue.id)
        assertEquals(BookTitle("Old Title"), bookCaptor.firstValue.title)
        assertEquals(BookPrice(100), bookCaptor.firstValue.price)
        assertEquals(BookStatus.UNPUBLISHED, bookCaptor.firstValue.status)
        assertEquals(listOf(AuthorId(1)), bookCaptor.firstValue.authorIds)
    }

    @Test
    fun updateBook_存在しないidを指定したらIllegalArgumentExceptionを投げる() {
        // Given
        `when`(bookRepository.findById(1)).thenReturn(null)

        // When
        val exception = assertThrows<IllegalArgumentException> {
            bookService.updateBook(1, "New Title", 200, "PUBLISHED", listOf(1))
        }

        // Then
        assertEquals("Book with id 1 not found", exception.message)
    }

    @Test
    fun updateBook_存在しない著者に紐付けようとしたらIllegalArgumentExceptionを投げる() {
        // Given
        val book = Book(BookId(1), BookTitle("Old Title"), BookPrice(100), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        `when`(bookRepository.findById(1)).thenReturn(book)
        `when`(authorRepository.findById(1)).thenReturn(null)

        // When
        val exception = assertThrows<IllegalArgumentException> {
            bookService.updateBook(1, "New Title", 200, "PUBLISHED", listOf(1))
        }

        // Then
        assertEquals("Author with id 1 not found", exception.message)
    }
}
