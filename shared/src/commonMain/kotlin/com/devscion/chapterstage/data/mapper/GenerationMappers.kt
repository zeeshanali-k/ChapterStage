package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.AgentTraceEventDto
import com.devscion.chapterstage.data.dto.AudienceLevelDto
import com.devscion.chapterstage.data.dto.ExperienceStyleDto
import com.devscion.chapterstage.data.dto.GenerationJobResponse
import com.devscion.chapterstage.data.dto.GenerationJobStartResponse
import com.devscion.chapterstage.data.dto.RecentGenerationJobDto
import com.devscion.chapterstage.data.dto.StartGenerationJobRequest
import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.AudienceLevel
import com.devscion.chapterstage.domain.model.ExperienceStyle
import com.devscion.chapterstage.domain.model.GenerationAgentStatus
import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.model.GenerationSettings
import com.devscion.chapterstage.domain.model.RecentGenerationJob
import kotlinx.serialization.json.JsonElement
import kotlin.math.roundToInt

fun GenerationSettings.toRequest(chapterId: String): StartGenerationJobRequest =
    StartGenerationJobRequest(
        chapterId = chapterId,
        audienceLevel = audienceLevel.toDto(),
        experienceStyle = experienceStyle.toDto(),
        targetScreenCount = targetScreenCount,
        enableAutoBrainstorm = autoBrainstorm,
    )

fun GenerationJobStartResponse.toDomain(settings: GenerationSettings): GenerationJob =
    GenerationJob(
        id = jobId,
        chapterId = chapterId,
        status = status,
        progress = 0,
        currentStep = "Queued",
        activeAgentId = null,
        agentStatuses = emptyMap(),
        events = emptyList(),
        elapsedSeconds = 0,
        publicUrl = null,
    )

fun GenerationJobResponse.toDomain(traceEvents: List<AgentTraceEvent> = emptyList()): GenerationJob {
    val normalizedStatus = status.lowercase()
    return GenerationJob(
        id = jobId,
        chapterId = chapterId,
        status = normalizedStatus,
        progress = progress.toProgressPercent(),
        currentStep = currentStep ?: normalizedStatus.toDisplayTitle(),
        activeAgentId = traceEvents.lastOrNull()?.agentId,
        agentStatuses = if (normalizedStatus == "completed") {
            traceEvents.map { it.agentId }.associateWith { GenerationAgentStatus.Completed }
        } else {
            traceEvents.lastOrNull()?.let { latest ->
                traceEvents.map { it.agentId }.associateWith { GenerationAgentStatus.Completed } +
                    (latest.agentId to GenerationAgentStatus.Active)
            } ?: emptyMap()
        },
        events = traceEvents,
        elapsedSeconds = 0,
        bandRoomId = bandRoomId,
        experienceId = experienceId,
        publicUrl = publicUrl,
        errorMessage = error?.message,
    )
}

fun AgentTraceEventDto.toDomain(): AgentTraceEvent =
    AgentTraceEvent(
        id = id,
        agentId = agentId ?: agentName.orEmpty().lowercase().replace(" ", "-").ifBlank { "agent" },
        agentName = agentName ?: "Agent",
        type = eventType,
        title = title,
        message = message,
        timestamp = createdAt,
        payload = payload.toPayloadPreview(),
        elapsedSeconds = elapsedSeconds,
    )

fun RecentGenerationJobDto.toDomain(): RecentGenerationJob =
    RecentGenerationJob(
        id = jobId,
        title = currentStep?.toDisplayTitle() ?: "Job ${jobId.take(8)}",
        book = "Chapter ${chapterId.take(8)}",
        status = status.toUiJobStatus(),
        style = currentStep?.toDisplayTitle() ?: status.toDisplayTitle(),
        updatedAt = updatedAt,
        progress = progress.toProgressPercent(),
        currentStep = currentStep,
        experienceId = experienceId,
        publicUrl = publicUrl,
        errorMessage = error?.message,
    )

private fun AudienceLevel.toDto(): AudienceLevelDto =
    when (this) {
        AudienceLevel.Beginner -> AudienceLevelDto.Beginner
        AudienceLevel.Intermediate -> AudienceLevelDto.Intermediate
        AudienceLevel.Expert -> AudienceLevelDto.Expert
    }

private fun ExperienceStyle.toDto(): ExperienceStyleDto =
    when (this) {
        ExperienceStyle.VisualStory -> ExperienceStyleDto.VisualStory
        ExperienceStyle.LectureMode -> ExperienceStyleDto.LectureMode
        ExperienceStyle.ConceptMapFirst -> ExperienceStyleDto.ConceptMapFirst
        ExperienceStyle.QuizFirst -> ExperienceStyleDto.QuizFirst
        ExperienceStyle.CaseStudy -> ExperienceStyleDto.CaseStudy
    }

private fun Double.toProgressPercent(): Int =
    if (this in 0.0..1.0) {
        (this * 100).roundToInt()
    } else {
        roundToInt()
    }.coerceIn(0, 100)

private fun String.toUiJobStatus(): String =
    when (lowercase()) {
        "completed" -> "ready"
        "queued", "extracting", "creating_band_room", "building_site", "publishing" -> "generating"
        "failed_agent_workflow" -> "failed"
        else -> lowercase()
    }

private fun String.toDisplayTitle(): String =
    replace('_', ' ')
        .replace('-', ' ')
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
        .ifBlank { "Running" }

private fun JsonElement?.toPayloadPreview(): String? =
    this?.toString()?.takeIf { it.isNotBlank() }
