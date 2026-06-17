package com.devscion.chapterstage.domain.usecase

import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.repository.GenerationRepository
import org.koin.core.annotation.Factory

@Factory
class GetAgentTraceUseCase(
    private val repository: GenerationRepository,
) {
    suspend operator fun invoke(jobId: String): Result<List<AgentTraceEvent>> = repository.getTrace(jobId)
}
