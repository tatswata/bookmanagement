package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Authors
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AuthorRepository(private val dsl: DSLContext) {

    fun findById(id: Int): AuthorsRecord? {
        return dsl.selectFrom(Authors.AUTHORS).where(Authors.AUTHORS.ID.eq(id)).fetchOneInto(AuthorsRecord::class.java)
    }

    fun save(name: String, birthDate: LocalDate): AuthorsRecord {
        return dsl.insertInto(Authors.AUTHORS)
            .columns(Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values(name, birthDate)
            .returning()
            .fetchOne()!!
    }

    fun update(id: Int, name: String, birthDate: LocalDate): Boolean {
        val updateCount = dsl.update(Authors.AUTHORS)
            .set(Authors.AUTHORS.NAME, name)
            .set(Authors.AUTHORS.BIRTH_DATE, birthDate)
            .where(Authors.AUTHORS.ID.eq(id))
            .execute()

        return updateCount > 0
    }
}
