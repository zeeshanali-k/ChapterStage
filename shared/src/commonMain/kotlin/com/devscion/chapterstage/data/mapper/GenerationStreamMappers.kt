package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.GenerationStreamEventDto
import com.devscion.chapterstage.data.remote.ServerSentEvent
import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.GenerationAgentStatus
import com.devscion.chapterstage.domain.model.GenerationJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.math.roundToInt

fun ServerSentEvent.toGenerationStreamEvent(json: Json): GenerationStreamEventDto? =
    runCatching {
        json.decodeFromString<GenerationStreamEventDto>(data)
    }.getOrNull()

fun GenerationStreamEventDto.toTraceEvent(
    streamEventName: String?,
    index: Int,
): AgentTraceEvent? {
    val resolvedTitle = title ?: currentStep ?: streamEventName?.toDisplayTitle()
    val resolvedMessage = message ?: error?.message ?: status

    if (resolvedTitle == null && resolvedMessage == null) return null

    val resolvedAgentName = agentName ?: activeAgentId?.toDisplayTitle() ?: "Agent"
    val resolvedAgentId = agentId ?: activeAgentId ?: resolvedAgentName.toAgentId()

    return AgentTraceEvent(
        id = id ?: "${jobId ?: "job"}-${streamEventName ?: "event"}-$index",
        agentId = resolvedAgentId,
        agentName = resolvedAgentName,
        type = eventType ?: streamEventName?.toDisplayTitle() ?: "Update",
        title = resolvedTitle ?: "Workflow update",
        message = resolvedMessage ?: "The workflow sent an update.",
        timestamp = createdAt ?: updatedAt ?: "now",
        payload = payload.toPayloadPreview(),
    )
}

fun GenerationStreamEventDto.toDomainJob(
    jobId: String,
    previous: GenerationJob?,
    traceEvents: List<AgentTraceEvent>,
): GenerationJob {
    val resolvedStatus = status?.lowercase()
        ?: if (publicUrl != null) "completed" else previous?.status ?: "running"
    val resolvedProgress = progress?.toProgressPercent()
        ?: previous?.progress
        ?: if (resolvedStatus == "completed") 100 else 0
    val resolvedActiveAgentId = activeAgentId
        ?: agentId
        ?: traceEvents.lastOrNull()?.agentId
        ?: previous?.activeAgentId

    return GenerationJob(
        id = this.jobId ?: previous?.id ?: jobId,
        chapterId = chapterId ?: previous?.chapterId ?: "",
        status = resolvedStatus,
        progress = if (resolvedStatus == "completed") 100 else resolvedProgress,
        currentStep = currentStep ?: title ?: previous?.currentStep ?: "Running",
        activeAgentId = if (resolvedStatus == "completed") null else resolvedActiveAgentId,
        agentStatuses = resolvedAgentStatuses(
            status = resolvedStatus,
            activeAgentId = resolvedActiveAgentId,
            traceEvents = traceEvents,
            previous = previous,
        ),
        events = traceEvents,
        elapsedSeconds = elapsedSeconds ?: previous?.elapsedSeconds ?: 0,
        bandRoomId = bandRoomId ?: previous?.bandRoomId,
        experienceId = experienceId ?: previous?.experienceId,
        publicUrl = publicUrl ?: previous?.publicUrl,
        errorMessage = error?.message ?: previous?.errorMessage,
    )
}

private fun GenerationStreamEventDto.resolvedAgentStatuses(
    status: String,
    activeAgentId: String?,
    traceEvents: List<AgentTraceEvent>,
    previous: GenerationJob?,
): Map<String, GenerationAgentStatus> {
    if (agentStatuses.isNotEmpty()) {
        return agentStatuses.mapValues { (_, value) -> value.toAgentStatus() }
    }

    if (status == "completed") {
        return (previous?.agentStatuses?.keys.orEmpty() + traceEvents.map { it.agentId })
            .associateWith { GenerationAgentStatus.Completed }
    }

    val completed = previous?.agentStatuses
        ?.filterValues { it == GenerationAgentStatus.Completed }
        ?.keys
        .orEmpty() + traceEvents.dropLast(1).map { it.agentId }

    val active = activeAgentId ?: traceEvents.lastOrNull()?.agentId
    return completed.associateWith { GenerationAgentStatus.Completed } +
        active?.let { mapOf(it to GenerationAgentStatus.Active) }.orEmpty()
}

private fun String.toAgentStatus(): GenerationAgentStatus =
    when (lowercase()) {
        "completed", "complete", "done" -> GenerationAgentStatus.Completed
        "active", "running", "working" -> GenerationAgentStatus.Active
        else -> GenerationAgentStatus.Waiting
    }

private fun String.toAgentId(): String =
    lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifBlank { "agent" }

private fun String.toDisplayTitle(): String =
    replace('_', ' ')
        .replace('-', ' ')
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
        .ifBlank { "Update" }

private fun Double.toProgressPercent(): Int =
    if (this in 0.0..1.0) {
        (this * 100).roundToInt()
    } else {
        roundToInt()
    }.coerceIn(0, 100)

private fun JsonElement?.toPayloadPreview(): String? =
    this?.toString()?.takeIf { it.isNotBlank() }
