package com.tatswata.bookmanagement.dto

import com.tatswata.bookmanagement.domain.author.Author

data class AuthorResponse(
    val id: Int,
    val name: String,
    val birthDate: String
) {
    constructor(author: Author) : this(
        id = author.id!!.id,
        name = author.name.name,
        birthDate = author.birthDate.birthDate.toString()
    )
}