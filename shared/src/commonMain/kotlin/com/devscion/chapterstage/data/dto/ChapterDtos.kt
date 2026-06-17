package com.devscion.chapterstage.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTextChapterRequest(
    @SerialName("book_title") val bookTitle: String? = null,
    @SerialName("chapter_title") val chapterTitle: String? = null,
    val text: String,
)

@Serializable
data class ChapterResponse(
    @SerialName("chapter_id") val chapterId: String,
    @SerialName("book_id") val bookId: String,
    val title: String? = null,
    @SerialName("source_type") val sourceType: String,
    @SerialName("created_at") val createdAt: String,
)
