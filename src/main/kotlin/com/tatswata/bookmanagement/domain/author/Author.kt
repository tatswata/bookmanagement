package com.tatswata.bookmanagement.domain.author

import java.time.LocalDate

class Author(
    val id: AuthorId?,
    name: AuthorName,
    birthDate: AuthorBirthDate
) {
    var name: AuthorName = name
        private set
    var birthDate: AuthorBirthDate = birthDate
        private set

    fun rename(newName: AuthorName) {
        this.name = newName
    }

    fun updateBirthDate(newBirthDate: AuthorBirthDate) {
        this.birthDate = newBirthDate
    }
}

@JvmInline
value class AuthorId(val id: Int)

@JvmInline
value class AuthorName(val name: String) {
    init {
        require(name.isNotBlank()) { "Name cannot be empty" }
        // 著者名の現実的な上限値として設定
        // 強い根拠はないので必要なら修正してください
        require(name.length <= 500) { "Name cannot exceed 500 characters" }
    }
}

@JvmInline
value class AuthorBirthDate(val birthDate: LocalDate) {
    init {
        require(birthDate.isBefore(LocalDate.now())) { "birthDate cannot be in the future" }
    }
}