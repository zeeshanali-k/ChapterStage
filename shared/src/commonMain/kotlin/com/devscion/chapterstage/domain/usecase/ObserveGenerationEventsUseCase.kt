package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.repository.GenerationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveGenerationEventsUseCase(
    private val repository: GenerationRepository,
) {
    operator fun invoke(jobId: String): Flow<GenerationJob> = repository.observeGeneration(jobId)
}
