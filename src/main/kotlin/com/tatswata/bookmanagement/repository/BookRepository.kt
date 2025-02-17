package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Books
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
}
