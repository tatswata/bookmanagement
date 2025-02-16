package com.tatswata.bookmanagement.controller

import com.tatswata.bookmanagement.service.BookService
import com.tatswata.bookmanagement.dto.BookResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): List<BookResponse> = bookService.getAllBooks()

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Int): BookResponse? = bookService.getBookById(id)

    @PostMapping
    fun createBook(
        @RequestBody createBookRequest: CreateBookRequest
    ): ResponseEntity<Void> {
        bookService.createBook(createBookRequest.title, createBookRequest.price, createBookRequest.status, createBookRequest.authorIds)
        return ResponseEntity.ok().build()
    }
}

data class CreateBookRequest(
    val title: String,
    val price: Int,
    val status: String,
    val authorIds: List<Int>
)
