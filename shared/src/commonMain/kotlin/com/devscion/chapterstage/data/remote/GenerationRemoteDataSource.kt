package com.devscion.chapterstage.data.remote

import com.devscion.chapterstage.data.dto.AgentTraceResponse
import com.devscion.chapterstage.data.dto.ExperienceMetadataResponse
import com.devscion.chapterstage.data.dto.GenerationJobResponse
import com.devscion.chapterstage.data.dto.GenerationJobStartResponse
import com.devscion.chapterstage.data.dto.RecentGenerationJobsResponse
import com.devscion.chapterstage.data.dto.StartGenerationJobRequest
import org.koin.core.annotation.Single

@Single
class GenerationRemoteDataSource(
    private val api: ChapterStageApi,
) {
    suspend fun startGenerationJob(request: StartGenerationJobRequest): Result<GenerationJobStartResponse> =
        apiResult {
            api.startGenerationJob(request)
        }

    suspend fun getGenerationJob(jobId: String): Result<GenerationJobResponse> =
        apiResult {
            api.getGenerationJob(jobId)
        }

    suspend fun getTrace(jobId: String): Result<AgentTraceResponse> =
        apiResult {
            api.getTrace(jobId)
        }

    suspend fun getRecentJobs(): Result<RecentGenerationJobsResponse> =
        apiResult {
            api.getRecentJobs()
        }

    suspend fun getExperienceMetadata(experienceId: String): Result<ExperienceMetadataResponse> =
        apiResult {
            api.getExperienceMetadata(experienceId = experienceId)
        }
}
