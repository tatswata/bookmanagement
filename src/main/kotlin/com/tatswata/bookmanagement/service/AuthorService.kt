package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.dto.AuthorResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    fun getAllAuthors(): List<AuthorResponse> {
        val authors = authorRepository.findAll()
        return authors.map { AuthorResponse(it.id, it.name, it.birthDate.toString()) }
    }

    fun getAuthorById(id: Int): AuthorResponse? {
        val author = authorRepository.findById(id)
        return author?.let { AuthorResponse(it.id, it.name, it.birthDate.toString()) }
    }

    fun createAuthor(name: String, birthDate: String): AuthorsRecord {
        val birthDateLocal = LocalDate.parse(birthDate)
        return authorRepository.save(name, birthDateLocal)
    }
}
