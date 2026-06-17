package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.core.common.DomainException
import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterInput
import com.devscion.chapterstage.domain.repository.ChapterRepository
import org.koin.core.annotation.Factory

@Factory
class CreateTextChapterUseCase(
    private val repository: ChapterRepository,
) {
    suspend operator fun invoke(input: ChapterInput): Result<Chapter> {
        val trimmedText = input.text.trim()
        if (trimmedText.length < MinimumChapterTextLength) {
            return Result.failure(
                DomainException.Validation(
                    code = "CHAPTER_TOO_SHORT",
                    message = "Add more chapter content before generating.",
                ),
            )
        }

        return repository.createTextChapter(input.copy(text = trimmedText))
    }

    private companion object {
        const val MinimumChapterTextLength = 500
    }
}
