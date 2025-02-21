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
import org.mockito.kotlin.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate



@SpringBootTest
class BookServiceTest {

    private val authorRepository: AuthorRepository = mock(AuthorRepository::class.java)
    private val bookRepository: BookRepository = mock(BookRepository::class.java)
    private val bookService = BookService(authorRepository, bookRepository)

    @Test
    fun 書籍を作成する_正常系() {
        `when`(authorRepository.findById(1)).thenReturn(Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01"))))
        `when`(bookRepository.save(any<Book>())).thenReturn(Book(BookId(1), BookTitle("Effective Java"), BookPrice(100), BookStatus.PUBLISHED, listOf(AuthorId(1))))

        val response = bookService.createBook("Effective Java", 100, "PUBLISHED", listOf(1))

        verify(bookRepository, times(1)).save(any<Book>())
        assertEquals("Effective Java", response.title)
        assertEquals(100, response.price)
        assertEquals("PUBLISHED", response.status)
    }

    @Test
    fun 書籍作成_存在しない著者に紐付けようとしたらIllegalArgumentExceptionを投げる() {
        `when`(authorRepository.findById(1)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            bookService.createBook("Effective Java", 100, "PUBLISHED", listOf(1))
        }

        assertEquals("Author with id 1 not found", exception.message)
    }

    @Test
    fun 書籍更新_正常系() {
        val book = Book(BookId(1), BookTitle("Old Title"), BookPrice(100), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        `when`(bookRepository.findById(1)).thenReturn(book)
        `when`(authorRepository.findById(1)).thenReturn(Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.parse("2000-01-01"))))
        `when`(bookRepository.save(any<Book>())).thenReturn(Book(BookId(1), BookTitle("Effective Java"), BookPrice(100), BookStatus.PUBLISHED, listOf(AuthorId(1))))

        val response = bookService.updateBook(1, "Effective Java", 100, "PUBLISHED", listOf(1))

        verify(bookRepository, times(1)).save(any<Book>())
        assertEquals("Effective Java", response!!.title)
        assertEquals(100, response!!.price)
        assertEquals("PUBLISHED", response!!.status)
    }


    @Test
    fun 書籍更新_存在しないidを指定したらIllegalArgumentExceptionを投げる() {
        `when`(bookRepository.findById(1)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            bookService.updateBook(1, "New Title", 200, "PUBLISHED", listOf(1))
        }

        assertEquals("Book with id 1 not found", exception.message)
    }

    @Test
    fun 書籍更新_存在しない著者に紐付けようとしたらIllegalArgumentExceptionを投げる() {
        val book = Book(BookId(1), BookTitle("Old Title"), BookPrice(100), BookStatus.UNPUBLISHED, listOf(AuthorId(1)))
        `when`(bookRepository.findById(1)).thenReturn(book)
        `when`(authorRepository.findById(1)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            bookService.updateBook(1, "New Title", 200, "PUBLISHED", listOf(1))
        }

        assertEquals("Author with id 1 not found", exception.message)
    }
}