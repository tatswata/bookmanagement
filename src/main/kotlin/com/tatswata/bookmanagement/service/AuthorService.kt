package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.dto.AuthorResponse
import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.repository.BookRepository
import com.tatswata.bookmanagement.repository.BooksAuthorsRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookAuthorRepository: BooksAuthorsRepository,
    private val bookRepository: BookRepository
) {

    fun getBooksWrittenByAuthor(authorId: Int): List<BookResponse> {
        // 著者IDに紐づく書籍ID一覧を取得
        val bookIds = bookAuthorRepository.findBooksByAuthorId(authorId)

        // 書籍情報を取得
        return bookRepository.findBooksByIds(bookIds).map { book ->
            BookResponse(
                id = book.id,
                title = book.title,
                price = book.price,
                status = book.status
            )
        }
    }

        fun createAuthor(name: String, birthDate: String): AuthorsRecord {
        val birthDateLocal = LocalDate.parse(birthDate)
        return authorRepository.save(name, birthDateLocal)
    }
}
