package com.devscion.chapterstage.domain.repository

import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.model.ChapterInput

interface ChapterRepository {
    suspend fun createTextChapter(input: ChapterInput): Result<Chapter>

    suspend fun uploadChapter(file: ChapterFile): Result<Chapter>
}
