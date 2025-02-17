package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Books
import com.tatswata.bookmanagement.db.tables.BooksAuthors
import com.tatswata.bookmanagement.db.tables.records.BooksRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val dsl: DSLContext) {

    fun findBooksByIds(bookIds: List<Int>): List<BooksRecord> {
        return dsl.selectFrom(Books.BOOKS)
            .where(Books.BOOKS.ID.`in`(bookIds))
            .fetchInto(BooksRecord::class.java)
    }

    fun save(title: String, price: Int, status: String): BooksRecord {
        return dsl.insertInto(Books.BOOKS)
            .columns(Books.BOOKS.TITLE, Books.BOOKS.PRICE, Books.BOOKS.STATUS)
            .values(title, price, status)
            .returning()
            .fetchOne()!!
    }

    fun update(id: Int, title: String, price: Int, status: String, authorIds: List<Int>): Boolean {
        val updateCount = dsl.update(Books.BOOKS)
            .set(Books.BOOKS.TITLE, title)
            .set(Books.BOOKS.PRICE, price)
            .set(Books.BOOKS.STATUS, status)
            .where(Books.BOOKS.ID.eq(id))
            .execute()

        if (updateCount > 0) {
            dsl.deleteFrom(BooksAuthors.BOOKS_AUTHORS)
                .where(BooksAuthors.BOOKS_AUTHORS.BOOK_ID.eq(id))
                .execute()

            authorIds.forEach { authorId ->
                dsl.insertInto(BooksAuthors.BOOKS_AUTHORS)
                    .set(BooksAuthors.BOOKS_AUTHORS.BOOK_ID, id)
                    .set(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID, authorId)
                    .execute()
            }
        }

        return updateCount > 0
    }
}
