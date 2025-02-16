package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.service.BookService
import com.tatswata.bookmanagement.dto.BookResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun createBook(
        @RequestBody createBookRequest: CreateBookRequest
    ): ResponseEntity<Void> {
        bookService.createBook(createBookRequest.title, createBookRequest.price, createBookRequest.status, createBookRequest.authorIds)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Int,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<Void> {
        return if (bookService.updateBook(id, request.title, request.price, request.status, request.authorIds)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class CreateBookRequest(
    val title: String,
    val price: Int,
    val status: String,
    val authorIds: List<Int>
)

// // ToDo: Entityを更新して永続化するようにしたらそれぞれのリクエストパラメータは任意にする
data class UpdateBookRequest(
    val title: String,
    val price: Int,
    val status: String,
    val authorIds: List<Int>
)
