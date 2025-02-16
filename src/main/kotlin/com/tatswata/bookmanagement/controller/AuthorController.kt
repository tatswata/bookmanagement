package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.service.AuthorService
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.dto.AuthorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController(private val authorService: AuthorService) {

    // 全著者を取得する
    @GetMapping
    fun getAllAuthors(): List<AuthorResponse> = authorService.getAllAuthors()

    // IDで著者を取得する
    @GetMapping("/{id}")
    fun getAuthorById(@PathVariable id: Int): AuthorResponse? = authorService.getAuthorById(id)

    // 著者を作成する
    @PostMapping
    fun createAuthor(
        @RequestBody createAuthorRequest: CreateAuthorRequest
    ): ResponseEntity<Void> {
        authorService.createAuthor(createAuthorRequest.name, createAuthorRequest.birthDate)
        return ResponseEntity.ok().build()
    }
}

data class CreateAuthorRequest(val name: String, val birthDate: String)
