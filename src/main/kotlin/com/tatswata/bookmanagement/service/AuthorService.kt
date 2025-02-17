package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.repository.AuthorRepository
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.dto.AuthorResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    fun createAuthor(name: String, birthDate: String): AuthorsRecord {
        val birthDateLocal = LocalDate.parse(birthDate)
        return authorRepository.save(name, birthDateLocal)
    }
}
