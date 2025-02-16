package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.service.AuthorService
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.dto.AuthorResponse
import com.tatswata.bookmanagement.dto.BookResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController(private val authorService: AuthorService) {

    @GetMapping("/{id}/books")
    fun getBooksWrittenByAuthor(@PathVariable id: Int): List<BookResponse> {
        return authorService.getBooksWrittenByAuthor(id)
    }

    @PostMapping
    fun createAuthor(
        @RequestBody createAuthorRequest: CreateAuthorRequest
    ): ResponseEntity<Void> {
        authorService.createAuthor(createAuthorRequest.name, createAuthorRequest.birthDate)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id}")
    fun updateAuthor(
        @PathVariable id: Int,
        @RequestBody request: UpdateAuthorRequest
    ): ResponseEntity<Void> {
        return if (authorService.updateAuthor(id, request.name, request.birthDate)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class CreateAuthorRequest(val name: String, val birthDate: String)
// ToDo: Entityを更新して永続化するようにしたらそれぞれのリクエストパラメータは任意にする
data class UpdateAuthorRequest(val name: String, val birthDate: String)
