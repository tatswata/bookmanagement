package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun createBook(
        @RequestBody createBookRequest: CreateBookRequest
    ): ResponseEntity<BookResponse> {
        val createdBookResponse = bookService.createBook(createBookRequest.title, createBookRequest.price, createBookRequest.status, createBookRequest.authorIds)
        return ResponseEntity.ok(createdBookResponse)
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Int,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<BookResponse> {
        val updateBookResponse = bookService.updateBook(id, request.title, request.price, request.status, request.authorIds)
        return if (updateBookResponse != null) {
            ResponseEntity.ok(updateBookResponse)
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

data class UpdateBookRequest(
    val title: String?,
    val price: Int?,
    val status: String?,
    val authorIds: List<Int>?
)
