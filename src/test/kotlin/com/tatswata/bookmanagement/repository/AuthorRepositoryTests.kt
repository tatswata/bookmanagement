package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Authors
import com.tatswata.bookmanagement.domain.author.*
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@JooqTest
@Import(AuthorRepository::class)
class AuthorRepositoryTests {

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun setUp() {
        dsl.execute("TRUNCATE TABLE authors RESTART IDENTITY CASCADE")
    }

    @Test
    fun findById_見つかった著者を返す() {
        // Given
        val authorId = dsl.insertInto(Authors.AUTHORS, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values("Test Author", LocalDate.parse("1980-01-01"))
            .returning(Authors.AUTHORS.ID)
            .fetchOne()!!.id

        // When
        val author = authorRepository.findById(authorId)

        // Then
        assertNotNull(author)
        assertEquals(authorId, author!!.id!!.value)
        assertEquals("Test Author", author.name.value)
        assertEquals(LocalDate.parse("1980-01-01"), author.birthDate.value)
    }

    @Test
    fun findById_著者が見つからない場合はnullを返す() {
        // Given
        val author = authorRepository.findById(999)

        // Then
        assertNull(author)
    }

    @Test
    fun save_IDがnullなら著者を新規作成() {
        // Given
        val author = Author(null, AuthorName("New Author"), AuthorBirthDate(LocalDate.parse("1990-01-01")))

        // When
        val savedAuthorOfReturn = authorRepository.save(author)

        // Then
        assertNotNull(savedAuthorOfReturn.id)
        assertEquals("New Author", savedAuthorOfReturn.name.value)
        assertEquals(LocalDate.parse("1990-01-01"), savedAuthorOfReturn.birthDate.value)

        val savedAuthorOfSelected = dsl.selectFrom(Authors.AUTHORS)
            .where(Authors.AUTHORS.ID.eq(savedAuthorOfReturn.id!!.value))
            .fetchOne()!!
        assertEquals("New Author", savedAuthorOfSelected.name)
        assertEquals(LocalDate.parse("1990-01-01"), savedAuthorOfSelected.birthDate)
    }

    @Test
    fun save_IDがあるなら著者を更新() {
        // Given
        val authorId = dsl.insertInto(Authors.AUTHORS, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values("Old Author", LocalDate.parse("1980-01-01"))
            .returning(Authors.AUTHORS.ID)
            .fetchOne()!!.id
        val author = Author(AuthorId(authorId), AuthorName("Updated Author"), AuthorBirthDate(LocalDate.parse("1990-01-01")))

        // When
        val updatedAuthorOfReturn = authorRepository.save(author)

        // Then
        assertEquals(authorId, updatedAuthorOfReturn.id!!.value)
        assertEquals("Updated Author", updatedAuthorOfReturn.name.value)
        assertEquals(LocalDate.parse("1990-01-01"), updatedAuthorOfReturn.birthDate.value)

        val updatedAuthorOfSelected = dsl.selectFrom(Authors.AUTHORS)
            .where(Authors.AUTHORS.ID.eq(authorId))
            .fetchOne()!!
        assertEquals("Updated Author", updatedAuthorOfSelected.name)
        assertEquals(LocalDate.parse("1990-01-01"), updatedAuthorOfSelected.birthDate)
    }
}
