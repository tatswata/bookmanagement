package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.service.AuthorService
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.dto.AuthorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController(private val authorService: AuthorService) {

    // ToDo: Authorに紐付く書籍の一覧を取得するエンドポイントを追加する
    // @GetMapping("/{id}/books")
    // fun getBooksWrittenByAuthor(@PathVariable id: Int): List<BookResponse> = authorService.getBooksWrittenByAuthor(id)

    @PostMapping
    fun createAuthor(
        @RequestBody createAuthorRequest: CreateAuthorRequest
    ): ResponseEntity<Void> {
        authorService.createAuthor(createAuthorRequest.name, createAuthorRequest.birthDate)
        return ResponseEntity.ok().build()
    }

    // ToDo: Authorを更新する
}

data class CreateAuthorRequest(val name: String, val birthDate: String)
