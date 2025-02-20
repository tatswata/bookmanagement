package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.dto.AuthorResponse
import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.service.AuthorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeParseException
import java.time.LocalDate

@RestController
@RequestMapping("/authors")
class AuthorController(private val authorService: AuthorService) {

    @GetMapping("/{id}/books")
    fun getBooksWrittenByAuthor(@PathVariable id: Int): ResponseEntity<List<BookResponse>> {
        return ResponseEntity.ok(authorService.getBooksWrittenByAuthor(id))
    }

    @PostMapping
    fun createAuthor(
        @RequestBody request: CreateAuthorRequest
    ): ResponseEntity<AuthorResponse> {
        val birthDateLocal = try {
            LocalDate.parse(request.birthDate)
        } catch (ex: DateTimeParseException) {
            throw IllegalArgumentException("Invalid format for birthDate")
        }

        val createdAuthorResponse = authorService.createAuthor(request.name, birthDateLocal)
        return ResponseEntity.ok(createdAuthorResponse)
    }

    @PutMapping("/{id}")
    fun updateAuthor(
        @PathVariable id: Int,
        @RequestBody request: UpdateAuthorRequest
    ): ResponseEntity<AuthorResponse> {
        val birthDateLocal = try {
            request.birthDate?.let { LocalDate.parse(it) }
        } catch (ex: DateTimeParseException) {
            throw IllegalArgumentException("Invalid format for birthDate")
        }

        val updatedAuthorResponse = authorService.updateAuthor(id, request.name, birthDateLocal)
        return updatedAuthorResponse?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }
}

data class CreateAuthorRequest(val name: String, val birthDate: String)
data class UpdateAuthorRequest(val name: String?, val birthDate: String?)
