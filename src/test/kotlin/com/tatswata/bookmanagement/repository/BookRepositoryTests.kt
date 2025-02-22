package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Authors
import com.tatswata.bookmanagement.db.tables.Books
import com.tatswata.bookmanagement.db.tables.BooksAuthors.BOOKS_AUTHORS
import com.tatswata.bookmanagement.domain.author.AuthorId
import com.tatswata.bookmanagement.domain.book.*
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@JooqTest
@Import(BookRepository::class)
class BookRepositoryTests {

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var bookRepository: BookRepository

    @BeforeEach
    fun setUp() {
        dsl.execute("TRUNCATE TABLE books_authors RESTART IDENTITY CASCADE")
        dsl.execute("TRUNCATE TABLE books RESTART IDENTITY CASCADE")
    }

    @Test
    fun findById_見つかった著者を返す() {
        // Given
        val bookId = dsl.insertInto(Books.BOOKS, Books.BOOKS.TITLE, Books.BOOKS.PRICE, Books.BOOKS.STATUS)
            .values("Test Book", 1000, "PUBLISHED")
            .returning(Books.BOOKS.ID)
            .fetchOne()!!.id
        val authorId = dsl.insertInto(Authors.AUTHORS)
            .columns(Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values("author name", LocalDate.parse("2000-01-01"))
            .returning(Authors.AUTHORS.ID, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .fetchOne()!!.id
        dsl.insertInto(BOOKS_AUTHORS)
            .set(BOOKS_AUTHORS.BOOK_ID, bookId)
            .set(BOOKS_AUTHORS.AUTHOR_ID, authorId)
            .execute()

        // When
        val book = bookRepository.findById(bookId)

        // Then
        assertNotNull(book)
        assertEquals("Test Book", book!!.title.value)
    }

    @Test
    fun findById_著者が見つからない場合はnullを返す() {
        // when
        val book = bookRepository.findById(999)

        // then
        assertNull(book)

    }

    @Test
    fun findBooksByAuthorId_著者に紐づく書籍のリストを返す() {
        // Given
        val authorId = 1
        val book1 = dsl.insertInto(Books.BOOKS, Books.BOOKS.TITLE, Books.BOOKS.PRICE, Books.BOOKS.STATUS)
            .values("Test Book", 1000, "PUBLISHED")
            .returning(Books.BOOKS.ID)
            .fetchOne()!!
        dsl.insertInto(BOOKS_AUTHORS)
            .set(BOOKS_AUTHORS.BOOK_ID, book1.id)
            .set(BOOKS_AUTHORS.AUTHOR_ID, authorId)
            .execute()

        val book2 = dsl.insertInto(Books.BOOKS, Books.BOOKS.TITLE, Books.BOOKS.PRICE, Books.BOOKS.STATUS)
            .values("Test Book2", 2000, "UNPUBLISHED")
            .returning(Books.BOOKS.ID)
            .fetchOne()!!
        dsl.insertInto(BOOKS_AUTHORS)
            .set(BOOKS_AUTHORS.BOOK_ID, book2.id)
            .set(BOOKS_AUTHORS.AUTHOR_ID, authorId)
            .execute()

        // When
        val books = bookRepository.findBooksByAuthorId(authorId)

        // Then
        assertEquals(2, books.size)

        assertEquals("Test Book", books[0].title.value)
        assertEquals(1000, books[0].price.value)
        assertEquals(BookStatus.PUBLISHED, books[0].status)

        assertEquals("Test Book2", books[1].title.value)
        assertEquals(2000, books[1].price.value)
        assertEquals(BookStatus.UNPUBLISHED, books[1].status)
    }

    @Test
    fun save_IDがnullなら新規作成() {
        // Given
        val authorId = dsl.insertInto(Authors.AUTHORS)
            .columns(Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values("author name", LocalDate.parse("2000-01-01"))
            .returning(Authors.AUTHORS.ID, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .fetchOne()!!.id
        val book = Book(null, BookTitle("New Book"), BookPrice(1500), BookStatus.PUBLISHED, listOf(AuthorId(authorId)))

        // When
        val savedBookOfReturn = bookRepository.save(book)

        // Then
        assertNotNull(savedBookOfReturn.id)
        assertEquals("New Book", savedBookOfReturn.title.value)
        assertEquals(1500, savedBookOfReturn.price.value)
        assertEquals(BookStatus.PUBLISHED, savedBookOfReturn.status)
        assertEquals(listOf(AuthorId(authorId)), savedBookOfReturn.authorIds)

        val savedBookOfSelected = dsl.selectFrom(Books.BOOKS).where(Books.BOOKS.ID.eq(savedBookOfReturn.id!!.value)).fetchOne()!!
        assertEquals("New Book", savedBookOfSelected.title)
        assertEquals(1500, savedBookOfSelected.price)
        assertEquals("PUBLISHED", savedBookOfSelected.status)

        val savedAuthorIds = dsl.select(BOOKS_AUTHORS.AUTHOR_ID)
            .from(BOOKS_AUTHORS)
            .where(BOOKS_AUTHORS.BOOK_ID.eq(savedBookOfReturn.id!!.value))
            .fetch()
            .map { AuthorId(it[BOOKS_AUTHORS.AUTHOR_ID]) }
        assertEquals(listOf(AuthorId(authorId)), savedAuthorIds)
    }

    @Test
    fun save_IDが無いなら更新() {
        // Given
        val authorId1 = dsl.insertInto(Authors.AUTHORS)
            .columns(Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values("author name1", LocalDate.parse("2000-01-01"))
            .returning(Authors.AUTHORS.ID, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .fetchOne()!!.id
        val authorId2 = dsl.insertInto(Authors.AUTHORS)
            .columns(Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values("author name1", LocalDate.parse("2000-01-01"))
            .returning(Authors.AUTHORS.ID, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .fetchOne()!!.id
        val bookId = dsl.insertInto(Books.BOOKS, Books.BOOKS.TITLE, Books.BOOKS.PRICE, Books.BOOKS.STATUS)
            .values("Old Book", 1000, "UNPUBLISHED")
            .returning(Books.BOOKS.ID)
            .fetchOne()!!.id
        dsl.insertInto(BOOKS_AUTHORS)
            .set(BOOKS_AUTHORS.BOOK_ID, bookId)
            .set(BOOKS_AUTHORS.AUTHOR_ID, authorId1)
            .execute()
        val book = Book(BookId(bookId), BookTitle("Updated Book"), BookPrice(2000), BookStatus.PUBLISHED, listOf(AuthorId(authorId2)))

        // When
        val updatedBookOfReturn = bookRepository.save(book)

        // Then
        assertEquals(bookId, updatedBookOfReturn.id!!.value)
        assertEquals("Updated Book", updatedBookOfReturn.title.value)
        assertEquals(2000, updatedBookOfReturn.price.value)
        assertEquals(BookStatus.PUBLISHED, updatedBookOfReturn.status)
        assertEquals(listOf(AuthorId(authorId2)), updatedBookOfReturn.authorIds)

        val updatedBookOfSelected = dsl.selectFrom(Books.BOOKS).where(Books.BOOKS.ID.eq(bookId)).fetchOne()!!
        assertEquals("Updated Book", updatedBookOfSelected.title)
        assertEquals(2000, updatedBookOfSelected.price)
        assertEquals("PUBLISHED", updatedBookOfSelected.status)

        val updatedAuthorIds = dsl.select(BOOKS_AUTHORS.AUTHOR_ID)
            .from(BOOKS_AUTHORS)
            .where(BOOKS_AUTHORS.BOOK_ID.eq(bookId))
            .fetch()
            .map { AuthorId(it[BOOKS_AUTHORS.AUTHOR_ID]) }
        assertEquals(listOf(AuthorId(authorId2)), updatedAuthorIds)
    }
}