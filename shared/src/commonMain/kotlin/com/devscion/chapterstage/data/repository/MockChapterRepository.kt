package com.devscion.chapterstage.data.repository

import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.model.ChapterInput
import com.devscion.chapterstage.domain.repository.ChapterRepository
import org.koin.core.annotation.Single

@Single
class MockChapterRepository : ChapterRepository {
    override suspend fun createTextChapter(input: ChapterInput): Result<Chapter> =
        Result.success(
            Chapter(
                id = MockWorkflowData.ChapterId,
                bookId = MockWorkflowData.BookId,
                title = input.chapterTitle ?: "Photosynthesis",
                sourceType = "text",
                createdAt = "2026-06-14T00:00:00Z",
            ),
        )

    override suspend fun uploadChapter(file: ChapterFile): Result<Chapter> =
        Result.success(
            Chapter(
                id = MockWorkflowData.ChapterId,
                bookId = MockWorkflowData.BookId,
                title = file.fileName.substringBeforeLast('.').ifBlank { "Uploaded chapter" },
                sourceType = file.extension,
                createdAt = "2026-06-14T00:00:00Z",
            ),
        )
}
