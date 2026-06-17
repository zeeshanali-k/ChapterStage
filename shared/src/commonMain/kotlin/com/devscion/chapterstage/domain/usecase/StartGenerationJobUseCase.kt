package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.model.GenerationSettings
import com.devscion.chapterstage.domain.repository.GenerationRepository
import org.koin.core.annotation.Factory

@Factory
class StartGenerationJobUseCase(
    private val repository: GenerationRepository,
) {
    suspend operator fun invoke(chapterId: String, settings: GenerationSettings): Result<GenerationJob> =
        repository.startGeneration(chapterId = chapterId, settings = settings)
}
