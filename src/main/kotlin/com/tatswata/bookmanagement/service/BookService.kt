package com.tatswata.bookmanagement.service

import com.tatswata.bookmanagement.domain.author.AuthorId
import com.tatswata.bookmanagement.domain.book.Book
import com.tatswata.bookmanagement.domain.book.BookPrice
import com.tatswata.bookmanagement.domain.book.BookStatus
import com.tatswata.bookmanagement.domain.book.BookTitle
import com.tatswata.bookmanagement.dto.BookResponse
import com.tatswata.bookmanagement.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository
) {

    @Transactional
    fun createBook(title: String, price: Int, status: String, authorIds: List<Int>): BookResponse {
        // ToDo: AuthorIdの存在チェック

        val bookTitle = BookTitle(title)
        val bookPrice = BookPrice(price)
        val bookStatus = BookStatus.valueOf(status)
        val bookAuthorIds = authorIds.map { AuthorId(it)}

        val book = Book(null, bookTitle, bookPrice, bookStatus, bookAuthorIds)

        val createdBook = bookRepository.save(book)
        return BookResponse(
            createdBook.id!!.id,
            createdBook.title.title,
            createdBook.price.price,
            createdBook.status.name
        )
    }

    @Transactional
    fun updateBook(id: Int, title: String?, price: Int?, status: String?, authorIds: List<Int>?): BookResponse? {
        val book = bookRepository.findById(id)
            ?: return null // ToDo: 例外分けて何が見つからなかったのか返せるようにした方が良さそう

        // ToDo: AuthorIdの存在チェック

        if (title != null) {
            val newTitle = BookTitle(title)
            book.updateTitle(newTitle)
        }
        if (price != null) {
            val newPrice = BookPrice(price)
            book.updatePrice(newPrice)
        }
        if (status != null && status == BookStatus.PUBLISHED.name) {
            book.publish()
        }
        if (authorIds != null) {
            val newAuthorIds: List<AuthorId> = authorIds.map { AuthorId(it) }
            book.updateAuthors(newAuthorIds)
        }

        val updatedBook = bookRepository.save(book)

        return BookResponse(
            updatedBook.id!!.id,
            updatedBook.title.title,
            updatedBook.price.price,
            updatedBook.status.name
        )
    }
}
