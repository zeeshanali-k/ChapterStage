package com.devscion.chapterstage.data.remote

import com.devscion.chapterstage.data.config.ChapterStageConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import io.ktor.sse.ServerSentEvent as KtorServerSentEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class GenerationEventStreamClient(
    private val client: HttpClient,
    private val config: ChapterStageConfig,
) {
    fun observeGenerationEvents(jobId: String): Flow<ServerSentEvent> = flow {
        client.sse(urlString = endpoint("generation-jobs/$jobId/events")) {
            incoming.collect { event ->
                event.toDomainEvent()?.let { emit(it) }
            }
        }
    }

    private fun KtorServerSentEvent.toDomainEvent(): ServerSentEvent? {
        val data = data ?: return null
        return ServerSentEvent(
            event = event,
            data = data,
            id = id,
        )
    }

    private fun endpoint(path: String): String =
        config.apiBaseUrl.trimEnd('/') + "/" + path.trimStart('/')
}
