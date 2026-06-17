package com.devscion.chapterstage.domain.model

data class Chapter(
    val id: String,
    val bookId: String,
    val title: String?,
    val sourceType: String,
    val createdAt: String,
)
