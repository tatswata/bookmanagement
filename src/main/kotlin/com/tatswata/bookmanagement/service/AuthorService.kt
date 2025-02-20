package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.domain.author.Author
import com.tatswata.bookmanagement.domain.author.AuthorBirthDate
import com.tatswata.bookmanagement.domain.author.AuthorName
import com.tatswata.bookmanagement.dto.AuthorResponse
import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {
    fun getBooksWrittenByAuthor(authorId: Int): List<BookResponse> {
        return bookRepository.findBooksByAuthorId(authorId).map { book ->
            BookResponse(book)
        }
    }

    @Transactional
    fun createAuthor(name: String, birthDate: LocalDate): AuthorResponse {
        val authorName = AuthorName(name)
        val authorBirthDate = AuthorBirthDate(birthDate)
        val author = Author(null, authorName, authorBirthDate)

        val createdAuthor = authorRepository.save(author)
        return AuthorResponse(createdAuthor)
    }

    @Transactional
    fun updateAuthor(id: Int, name: String?, birthDate: LocalDate?): AuthorResponse? {
        val author = authorRepository.findById(id)
            ?: return null

        if (name != null) {
            val newName = AuthorName(name)
            author.rename(newName)
        }
        if (birthDate != null) {
            val newBirthDate = AuthorBirthDate(birthDate)
            author.updateBirthDate(newBirthDate)
        }

        val updatedAuthor = authorRepository.save(author)
        return AuthorResponse(updatedAuthor)
    }
}
