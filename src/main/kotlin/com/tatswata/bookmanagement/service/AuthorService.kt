package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.dto.AuthorResponse
import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.repository.BookRepository
import com.tatswata.bookmanagement.repository.BooksAuthorsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookAuthorRepository: BooksAuthorsRepository,
    private val bookRepository: BookRepository
) {

    fun getBooksWrittenByAuthor(authorId: Int): List<BookResponse> {
        val bookIds = bookAuthorRepository.findBooksByAuthorId(authorId)

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

    @Transactional
    fun updateAuthor(id: Int, name: String, birthDate: String): Boolean {
        // ToDo: Entityを更新してsaveするだけにする

        val birthDateLocal = LocalDate.parse(birthDate)
        return authorRepository.update(id, name, birthDateLocal)
    }
}
