package com.tatswata.bookmanagement.repository

import com.tatswata.bookmanagement.db.tables.Authors
import com.tatswata.bookmanagement.db.tables.records.AuthorsRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AuthorRepository(private val dsl: DSLContext) {

    // 全著者を取得
    fun findAll(): List<AuthorsRecord> {
        return dsl.selectFrom(Authors.AUTHORS).fetchInto(AuthorsRecord::class.java)
    }

    // IDで著者を検索
    fun findById(id: Int): AuthorsRecord? {
        return dsl.selectFrom(Authors.AUTHORS).where(Authors.AUTHORS.ID.eq(id)).fetchOneInto(AuthorsRecord::class.java)
    }

    // 著者の作成
    fun save(name: String, birthDate: LocalDate): AuthorsRecord {
        return dsl.insertInto(Authors.AUTHORS)
            .columns(Authors.AUTHORS.NAME, Authors.AUTHORS.BIRTH_DATE)
            .values(name, birthDate)  // birthDateをLocalDate型として扱う
            .returning()
            .fetchOne()!!
    }
}
