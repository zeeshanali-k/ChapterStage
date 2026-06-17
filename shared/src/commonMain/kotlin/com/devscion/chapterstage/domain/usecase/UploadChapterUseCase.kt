package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.core.common.DomainException
import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.repository.ChapterRepository
import org.koin.core.annotation.Factory

@Factory
class UploadChapterUseCase(
    private val repository: ChapterRepository,
) {
    suspend operator fun invoke(file: ChapterFile): Result<Chapter> {
        if (file.extension !in SupportedExtensions) {
            return Result.failure(
                DomainException.Validation(
                    code = "INVALID_FILE_TYPE",
                    message = "Only PDF and TXT files are supported right now.",
                ),
            )
        }

        return repository.uploadChapter(file)
    }

    private companion object {
        val SupportedExtensions = setOf("pdf", "txt")
    }
}
