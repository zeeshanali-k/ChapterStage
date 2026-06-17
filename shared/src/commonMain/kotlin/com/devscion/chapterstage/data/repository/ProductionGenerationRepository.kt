package com.devscion.chapterstage.data.repository

import com.devscion.chapterstage.data.mapper.toDomain
import com.devscion.chapterstage.data.mapper.toDomainJob
import com.devscion.chapterstage.data.mapper.toGenerationStreamEvent
import com.devscion.chapterstage.data.mapper.toRequest
import com.devscion.chapterstage.data.mapper.toTraceEvent
import com.devscion.chapterstage.data.remote.GenerationEventStreamClient
import com.devscion.chapterstage.data.remote.GenerationRemoteDataSource
import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.ExperienceMetadata
import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.model.GenerationSettings
import com.devscion.chapterstage.domain.model.RecentGenerationJob
import com.devscion.chapterstage.domain.repository.GenerationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ProductionGenerationRepository(
    private val remoteDataSource: GenerationRemoteDataSource,
    private val eventStreamClient: GenerationEventStreamClient,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
) : GenerationRepository {
    override suspend fun getRecentJobs(): Result<List<RecentGenerationJob>> =
        withContext(ioDispatcher) {
            remoteDataSource.getRecentJobs().map { response ->
                response.jobs.map { it.toDomain() }
            }
        }

    override suspend fun startGeneration(
        chapterId: String,
        settings: GenerationSettings,
    ): Result<GenerationJob> =
        withContext(ioDispatcher) {
            remoteDataSource.startGenerationJob(settings.toRequest(chapterId)).map { it.toDomain(settings) }
        }

    override fun observeGeneration(jobId: String): Flow<GenerationJob> =
        observeGenerationFromStream(jobId)
            .catch {
                emitAll(observeGenerationByPolling(jobId))
            }
            .flowOn(ioDispatcher)

    override suspend fun getTrace(jobId: String): Result<List<AgentTraceEvent>> =
        withContext(ioDispatcher) {
            remoteDataSource.getTrace(jobId).map { response ->
                response.events.map { it.toDomain() }
            }
        }

    override suspend fun getExperienceMetadata(experienceId: String): Result<ExperienceMetadata> =
        withContext(ioDispatcher) {
            remoteDataSource.getExperienceMetadata(experienceId = experienceId).map { it.toDomain() }
        }

    private companion object {
        const val PollingIntervalMillis = 2_000L

        val StreamJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    private fun observeGenerationFromStream(jobId: String): Flow<GenerationJob> = flow {
        // SSE delivers incremental state; keep the last snapshot locally to merge sparse event payloads.
        var previous: GenerationJob? = null
        val traceEvents = mutableListOf<AgentTraceEvent>()
        var eventIndex = 0

        eventStreamClient.observeGenerationEvents(jobId)
            .transformWhile { event ->
                val streamEvent = event.toGenerationStreamEvent(StreamJson) ?: return@transformWhile true
                streamEvent.toTraceEvent(streamEventName = event.event, index = eventIndex)?.let { traceEvent ->
                    traceEvents += traceEvent
                    eventIndex += 1
                }

                val job = streamEvent.toDomainJob(
                    jobId = jobId,
                    previous = previous,
                    traceEvents = traceEvents,
                )
                previous = job
                emit(job)
                !job.isTerminal
            }
            .collect { job -> emit(job) }

        if (previous?.isTerminal != true) {
            emitAll(observeGenerationByPolling(jobId))
        }
    }

    private fun observeGenerationByPolling(jobId: String): Flow<GenerationJob> = flow {
        while (true) {
            val traceEvents = getTrace(jobId).getOrElse { emptyList() }
            val job = remoteDataSource.getGenerationJob(jobId)
                .getOrThrow()
                .toDomain(traceEvents = traceEvents)

            emit(job)
            if (job.isTerminal) break

            delay(PollingIntervalMillis)
        }
    }
}
