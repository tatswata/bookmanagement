package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Books
import com.tatswata.bookmanagement.db.tables.BooksAuthors.BOOKS_AUTHORS
import com.tatswata.bookmanagement.db.tables.records.BooksRecord
import com.tatswata.bookmanagement.domain.author.AuthorId
import com.tatswata.bookmanagement.domain.book.Book
import com.tatswata.bookmanagement.domain.book.BookId
import com.tatswata.bookmanagement.domain.book.BookPrice
import com.tatswata.bookmanagement.domain.book.BookStatus
import com.tatswata.bookmanagement.domain.book.BookTitle
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val dsl: DSLContext) {

    fun findById(id: Int): Book? {
        val bookRecord = dsl.selectFrom(Books.BOOKS).where(Books.BOOKS.ID.eq(id)).fetchOneInto(BooksRecord::class.java)
            ?: return null

        val bookId = BookId(bookRecord.id)
        val bookTitle = BookTitle(bookRecord.title)
        val bookPrice = BookPrice(bookRecord.price)
        val bookStatus = BookStatus.valueOf(bookRecord.status)
        val authorIds = dsl.select(BOOKS_AUTHORS.AUTHOR_ID)
            .from(BOOKS_AUTHORS)
            .where(BOOKS_AUTHORS.BOOK_ID.eq(bookId.id))
            .fetch()
            .map { AuthorId(it[BOOKS_AUTHORS.AUTHOR_ID]) }

        return Book(bookId, bookTitle, bookPrice, bookStatus, authorIds)
    }

    fun findBooksByAuthorId(authorId: Int): List<Book> {
        val bookIds = dsl.select(BOOKS_AUTHORS.BOOK_ID)
            .from(BOOKS_AUTHORS)
            .where(BOOKS_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch()
            .map { it[BOOKS_AUTHORS.BOOK_ID] }

        return bookIds.mapNotNull { findById(it) }
    }

    fun save(book: Book): Book {
        if (book.id == null) { // insert
            val record = dsl.insertInto(Books.BOOKS)
                .columns(Books.BOOKS.TITLE, Books.BOOKS.PRICE, Books.BOOKS.STATUS)
                .values(book.title.title, book.price.price, book.status.name)
                .returning()
                .fetchOne()!!

            val bookId = BookId(record.id)
            val bookTitle = BookTitle(record.title)
            val bookPrice = BookPrice(record.price)
            val bookStatus = BookStatus.valueOf(record.status)

            book.authorIds.forEach { authorId ->
                dsl.insertInto(BOOKS_AUTHORS)
                    .set(BOOKS_AUTHORS.BOOK_ID, bookId.id)
                    .set(BOOKS_AUTHORS.AUTHOR_ID, authorId.id)
                    .execute()
            }
            val authorIds = dsl.select(BOOKS_AUTHORS.AUTHOR_ID)
                .from(BOOKS_AUTHORS)
                .where(BOOKS_AUTHORS.BOOK_ID.eq(bookId.id))
                .fetch()
                .map { AuthorId(it[BOOKS_AUTHORS.AUTHOR_ID]) }

            return Book(bookId, bookTitle, bookPrice, bookStatus, authorIds)
        } else { // update
            dsl.update(Books.BOOKS)
                .set(Books.BOOKS.TITLE, book.title.title)
                .set(Books.BOOKS.PRICE, book.price.price)
                .set(Books.BOOKS.STATUS, book.status.name)
                .where(Books.BOOKS.ID.eq(book.id.id))
                .execute()

            dsl.deleteFrom(BOOKS_AUTHORS)
                .where(BOOKS_AUTHORS.BOOK_ID.eq(book.id.id))
                .execute()
            book.authorIds.forEach { authorId ->
                dsl.insertInto(BOOKS_AUTHORS)
                    .set(BOOKS_AUTHORS.BOOK_ID, book.id.id)
                    .set(BOOKS_AUTHORS.AUTHOR_ID, authorId.id)
                    .execute()
            }

            return book
        }
    }
}
