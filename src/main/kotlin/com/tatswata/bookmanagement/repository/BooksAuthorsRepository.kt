package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.BooksAuthors
import com.tatswata.bookmanagement.db.tables.records.BooksAuthorsRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BooksAuthorsRepository(private val dsl: DSLContext) {

    fun createBookAuthor(bookId: Int, authorId: Int): BooksAuthorsRecord {
        return dsl.insertInto(BooksAuthors.BOOKS_AUTHORS)
            .set(BooksAuthors.BOOKS_AUTHORS.BOOK_ID, bookId)
            .set(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID, authorId)
            .returning()
            .fetchOne()!!
    }

    fun findAuthorsByBookId(bookId: Int): List<Int> {
        return dsl.select(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID)
            .from(BooksAuthors.BOOKS_AUTHORS)
            .where(BooksAuthors.BOOKS_AUTHORS.BOOK_ID.eq(bookId))
            .fetch()
            .map { it[BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID] }
    }

    fun findBooksByAuthorId(authorId: Int): List<Int> {
        return dsl.select(BooksAuthors.BOOKS_AUTHORS.BOOK_ID)
            .from(BooksAuthors.BOOKS_AUTHORS)
            .where(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch()
            .map { it[BooksAuthors.BOOKS_AUTHORS.BOOK_ID] }
    }

    fun deleteBookAuthor(bookId: Int, authorId: Int) {
        dsl.deleteFrom(BooksAuthors.BOOKS_AUTHORS)
            .where(BooksAuthors.BOOKS_AUTHORS.BOOK_ID.eq(bookId))
            .and(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID.eq(authorId))
            .execute()
    }
}
