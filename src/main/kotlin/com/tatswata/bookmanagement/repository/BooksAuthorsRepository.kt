package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.BooksAuthors
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BooksAuthorsRepository(private val dsl: DSLContext) {


    // ToDo: bookRepositoryに寄せる
    fun findBooksByAuthorId(authorId: Int): List<Int> {
        return dsl.select(BooksAuthors.BOOKS_AUTHORS.BOOK_ID)
            .from(BooksAuthors.BOOKS_AUTHORS)
            .where(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch()
            .map { it[BooksAuthors.BOOKS_AUTHORS.BOOK_ID] }
    }
}
