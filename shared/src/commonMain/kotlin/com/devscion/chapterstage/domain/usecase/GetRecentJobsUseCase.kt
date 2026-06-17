package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.domain.model.RecentGenerationJob
import com.devscion.chapterstage.domain.repository.GenerationRepository
import org.koin.core.annotation.Factory

@Factory
class GetRecentJobsUseCase(
    private val repository: GenerationRepository,
) {
    suspend operator fun invoke(): Result<List<RecentGenerationJob>> = repository.getRecentJobs()
}
