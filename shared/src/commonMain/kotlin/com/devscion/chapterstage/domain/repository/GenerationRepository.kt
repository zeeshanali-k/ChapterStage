package com.devscion.chapterstage.domain.repository

import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.ExperienceMetadata
import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.model.GenerationSettings
import com.devscion.chapterstage.domain.model.RecentGenerationJob
import kotlinx.coroutines.flow.Flow

interface GenerationRepository {
    suspend fun getRecentJobs(): Result<List<RecentGenerationJob>>

    suspend fun startGeneration(chapterId: String, settings: GenerationSettings): Result<GenerationJob>

    fun observeGeneration(jobId: String): Flow<GenerationJob>

    suspend fun getTrace(jobId: String): Result<List<AgentTraceEvent>>

    suspend fun getExperienceMetadata(experienceId: String): Result<ExperienceMetadata>
}
