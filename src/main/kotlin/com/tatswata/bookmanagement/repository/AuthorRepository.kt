package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Authors
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import com.tatswata.bookmanagement.domain.author.Author
import com.tatswata.bookmanagement.domain.author.AuthorBirthDate
import com.tatswata.bookmanagement.domain.author.AuthorId
import com.tatswata.bookmanagement.domain.author.AuthorName
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(private val dsl: DSLContext) {

    fun findById(id: Int): Author? {
        val record = dsl.selectFrom(Authors.AUTHORS).where(Authors.AUTHORS.ID.eq(id)).fetchOneInto(AuthorsRecord::class.java)
            ?: return null

        val authorId = AuthorId(record.id)
        val authorName = AuthorName(record.name)
        val authorBirthDate = AuthorBirthDate(record.birthDate)

        return Author(authorId, authorName, authorBirthDate)
    }

    fun save(author: Author): Author {
        if (author.id == null) { // insert
            val record = dsl.insertInto(Authors.AUTHORS)
                .columns(Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
                .values(author.name.name, author.birthDate.birthDate)
                .returning(Authors.AUTHORS.ID, Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
                .fetchOne()!!

            val authorId = AuthorId(record.id)
            val authorName = AuthorName(record.name)
            val authorBirthDate = AuthorBirthDate(record.birthDate)

            return Author(authorId, authorName, authorBirthDate)
        } else { // update
            dsl.update(Authors.AUTHORS)
                .set(Authors.AUTHORS.NAME, author.name.name)
                .set(Authors.AUTHORS.BIRTH_DATE, author.birthDate.birthDate)
                .where(Authors.AUTHORS.ID.eq(author.id.id))
                .execute()

            return author
        }
    }
}
