package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.domain.model.ExperienceMetadata
import com.devscion.chapterstage.domain.repository.GenerationRepository
import org.koin.core.annotation.Factory

@Factory
class GetExperienceMetadataUseCase(
    private val repository: GenerationRepository,
) {
    suspend operator fun invoke(experienceId: String): Result<ExperienceMetadata> =
        repository.getExperienceMetadata(experienceId = experienceId)
}
