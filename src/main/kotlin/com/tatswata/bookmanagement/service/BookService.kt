package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.db.tables.records.BooksRecord
import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.repository.BookRepository
import com.tatswata.bookmanagement.repository.BooksAuthorsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val bookAuthorRepository: BooksAuthorsRepository
) {

    // ToDo: ドメインモデルを使うようにする
    @Transactional
    fun createBook(title: String, price: Int, status: String, authorIds: List<Int>): BooksRecord {
        val book = bookRepository.save(title, price, status)
        authorIds.forEach { authorId ->
            if (authorRepository.findById(authorId) != null) {
                bookAuthorRepository.createBookAuthor(book.id, authorId)
            }
        }
        return book
    }

    // ToDo: ドメインモデルを使うようにする
    @Transactional
    fun updateBook(id: Int, title: String, price: Int, status: String, authorIds: List<Int>): Boolean {
        return bookRepository.update(id, title, price, status, authorIds)
    }

}
