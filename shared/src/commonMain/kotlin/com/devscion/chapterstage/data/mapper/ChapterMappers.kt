package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.ChapterResponse
import com.devscion.chapterstage.data.dto.CreateTextChapterRequest
import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterInput

fun ChapterInput.toRequest(): CreateTextChapterRequest =
    CreateTextChapterRequest(
        bookTitle = bookTitle,
        chapterTitle = chapterTitle,
        text = text,
    )

fun ChapterResponse.toDomain(): Chapter =
    Chapter(
        id = chapterId,
        bookId = bookId,
        title = title,
        sourceType = sourceType,
        createdAt = createdAt,
    )
