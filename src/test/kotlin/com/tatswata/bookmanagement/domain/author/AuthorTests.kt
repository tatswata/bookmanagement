package com.tatswata.bookmanagement.domain.author

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class AuthorTests {

    @Test
    fun 有効なデータで著者を作成できる() {
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.of(1990, 1, 1)))

        assertNotNull(author)
        assertEquals("John Doe", author.name.value)
        assertEquals(LocalDate.of(1990, 1, 1), author.birthDate.value)
    }

    @Test
    fun 空の名前では著者を作成できない() {
        val exception = assertThrows<IllegalArgumentException> {
            Author(AuthorId(1), AuthorName(""), AuthorBirthDate(LocalDate.of(1990, 1, 1)))
        }

        assertEquals("Name cannot be empty", exception.message)
    }

    @Test
    fun 名前が最大長ちょうどの著者を作成できる() {
        val longName = "a".repeat(500)

        val author = Author(AuthorId(1), AuthorName(longName), AuthorBirthDate(LocalDate.of(1990, 1, 1)))

        assertEquals(500, author.name.value.length)
    }

    @Test
    fun 名前が最大長を超える著者は作成できない() {
        val longName = "a".repeat(501)

        val exception = assertThrows<IllegalArgumentException> {
            Author(AuthorId(1), AuthorName(longName), AuthorBirthDate(LocalDate.of(1990, 1, 1)))
        }

        assertEquals("Name cannot exceed 500 characters", exception.message)
    }

    @Test
    fun 当日の生年月日で著者を作成できる() {
        val today = LocalDate.now()

        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(today))

        assertEquals(today, author.birthDate.value)
    }

    @Test
    fun 未来の生年月日では著者は作成できない() {
        val futureDate = LocalDate.now().plusDays(1)

        val exception = assertThrows<IllegalArgumentException> {
            Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(futureDate))
        }

        assertEquals("birthDate cannot be in the future", exception.message)
    }

    @Test
    fun 著者の名前を変更できる() {
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.of(1990, 1, 1)))

        author.rename(AuthorName("Jane Doe Jr."))

        assertEquals("Jane Doe Jr.", author.name.value)
    }

    @Test
    fun 著者の生年月日を更新できる() {
        val author = Author(AuthorId(1), AuthorName("John Doe"), AuthorBirthDate(LocalDate.of(1990, 1, 1)))
        val newBirthDate = LocalDate.of(1991, 2, 2)

        author.updateBirthDate(AuthorBirthDate(newBirthDate))

        assertEquals(newBirthDate, author.birthDate.value)
    }
}