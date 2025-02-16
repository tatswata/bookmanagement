package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.repository.BookRepository
import com.tatswata.bookmanagement.repository.BooksAuthorsRepository
import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.db.tables.records.BooksRecord
import com.tatswata.bookmanagement.dto.BookResponse
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val bookAuthorRepository: BooksAuthorsRepository
) {

    fun getAllBooks(): List<BookResponse> {
        var books = bookRepository.findAll()
        return books.map { BookResponse(it.id, it.title, it.price, it.status)}

    }

    fun getBookById(id: Int): BookResponse? {
        var book = bookRepository.findById(id)
        return book?.let {BookResponse(it.id, it.title, it.price, it.status)}
    }

    fun createBook(title: String, price: Int, status: String, authorIds: List<Int>): BooksRecord {
        val book = bookRepository.save(title, price, status)
        authorIds.forEach { authorId ->
            if (authorRepository.findById(authorId) != null) {
                bookAuthorRepository.createBookAuthor(book.id, authorId)
            }
        }
        return book
    }
}
