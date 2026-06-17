package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.core.common.DomainException
import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.model.ChapterInput
import com.devscion.chapterstage.domain.repository.ChapterRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CreateTextChapterUseCaseTest {
    private val repository = FakeChapterRepository()
    private val useCase = CreateTextChapterUseCase(repository)

    @Test
    fun `given short chapter text, when invoked, then validation error is returned`() = runTest {
        val result = useCase(
            ChapterInput(
                bookTitle = null,
                chapterTitle = null,
                text = "too short",
            ),
        )

        val exception = result.exceptionOrNull()
        assertIs<DomainException.Validation>(exception)
        assertEquals("CHAPTER_TOO_SHORT", exception.code)
    }

    @Test
    fun `given valid chapter text, when invoked, then repository receives trimmed text`() = runTest {
        val text = " ".repeat(2) + "a".repeat(500) + " "

        val result = useCase(
            ChapterInput(
                bookTitle = "Book",
                chapterTitle = "Chapter",
                text = text,
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals("a".repeat(500), repository.lastInput?.text)
    }
}

private class FakeChapterRepository : ChapterRepository {
    var lastInput: ChapterInput? = null
        private set

    override suspend fun createTextChapter(input: ChapterInput): Result<Chapter> {
        lastInput = input
        return Result.success(
            Chapter(
                id = "chapter-1",
                bookId = "book-1",
                title = input.chapterTitle,
                sourceType = "text",
                createdAt = "now",
            ),
        )
    }

    override suspend fun uploadChapter(file: ChapterFile): Result<Chapter> =
        error("Upload is not part of this use case test")
}
