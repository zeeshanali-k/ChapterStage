package com.devscion.chapterstage.presentation.mapper

import com.devscion.chapterstage.core.common.DomainException
import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.AudienceLevel
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.model.ChapterInput
import com.devscion.chapterstage.domain.model.ExperienceStyle
import com.devscion.chapterstage.domain.model.GenerationAgentStatus
import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.model.GenerationSettings
import com.devscion.chapterstage.domain.model.RecentGenerationJob
import com.devscion.chapterstage.presentation.model.AgentStatus
import com.devscion.chapterstage.presentation.model.AgentUiModel
import com.devscion.chapterstage.presentation.model.AudienceLevelOption
import com.devscion.chapterstage.presentation.model.ChapterSourceDraft
import com.devscion.chapterstage.presentation.model.ExperienceStyleOption
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft
import com.devscion.chapterstage.presentation.model.GenerationSnapshot
import com.devscion.chapterstage.presentation.model.PickedChapterFile
import com.devscion.chapterstage.presentation.model.RecentJobUiModel
import com.devscion.chapterstage.presentation.model.TraceEventUiModel

fun ChapterSourceDraft.toChapterInput(fallbackText: String): ChapterInput =
    ChapterInput(
        bookTitle = bookTitle.trim().ifBlank { null },
        chapterTitle = chapterTitle.trim().ifBlank { null },
        text = text.ifBlank { fallbackText },
    )

fun PickedChapterFile.toDomain(): ChapterFile =
    ChapterFile(
        fileName = fileName,
        bytes = bytes,
        contentType = contentType,
    )

fun GenerationSettingsDraft.toDomain(): GenerationSettings =
    GenerationSettings(
        audienceLevel = audienceLevel.toDomain(),
        experienceStyle = experienceStyle.toDomain(),
        targetScreenCount = targetScreenCount,
        autoBrainstorm = autoBrainstorm,
    )

fun RecentGenerationJob.toUiModel(): RecentJobUiModel =
    RecentJobUiModel(
        id = id,
        title = title,
        book = book,
        status = status,
        style = style,
        updatedAt = updatedAt,
        progress = progress,
        currentStep = currentStep,
        experienceId = experienceId,
        publicUrl = publicUrl,
        errorMessage = errorMessage,
    )

fun GenerationJob.toUiSnapshot(agents: List<AgentUiModel>): GenerationSnapshot {
    val resolvedStatuses = agents.associate { agent ->
        val status = agentStatuses[agent.id]?.toUiStatus() ?: AgentStatus.Waiting
        agent.id to status
    }
    val effectiveActiveAgentId = activeAgentId ?: if (!isComplete) "structure" else null
    val finalStatuses = if (
        effectiveActiveAgentId != null &&
        resolvedStatuses[effectiveActiveAgentId] == AgentStatus.Waiting
    ) {
        resolvedStatuses + (effectiveActiveAgentId to AgentStatus.Active)
    } else {
        resolvedStatuses
    }

    return GenerationSnapshot(
        jobId = id,
        experienceId = experienceId,
        statuses = finalStatuses,
        activeAgentId = effectiveActiveAgentId,
        events = events.map { it.toUiModel() },
        progress = progress,
        elapsedSeconds = elapsedSeconds,
        isComplete = isComplete,
        publicUrl = publicUrl,
    )
}

fun DomainException.toUserMessage(): String =
    when (this) {
        is DomainException.Validation -> when (code) {
            "INVALID_FILE_TYPE" -> "Only PDF and TXT files are supported right now."
            "FILE_TOO_LARGE" -> "This file is too large for the MVP. Try a smaller chapter."
            "CHAPTER_TOO_SHORT" -> "Add more chapter content before generating."
            "EXTRACTION_FAILED" -> "We could not extract readable text from this file."
            "FILE_PICKER_CANCELLED" -> "No file was selected."
            "FILE_PICKER_FAILED" -> message ?: "We could not read that file. Try another PDF or TXT."
            else -> message ?: "Check your input and try again."
        }
        is DomainException.Network -> "We could not reach ChapterStage. Check your connection and try again."
        is DomainException.NotFound -> "That ChapterStage resource could not be found."
        is DomainException.Server -> message ?: "ChapterStage is having trouble right now. Try again."
        DomainException.Unauthorized -> "Your session is not authorized for this action."
        is DomainException.Unknown -> "Something unexpected happened. Try again."
    }

private fun AudienceLevelOption.toDomain(): AudienceLevel =
    when (this) {
        AudienceLevelOption.Beginner -> AudienceLevel.Beginner
        AudienceLevelOption.Intermediate -> AudienceLevel.Intermediate
        AudienceLevelOption.Expert -> AudienceLevel.Expert
    }

private fun ExperienceStyleOption.toDomain(): ExperienceStyle =
    when (this) {
        ExperienceStyleOption.VisualStory -> ExperienceStyle.VisualStory
        ExperienceStyleOption.LectureMode -> ExperienceStyle.LectureMode
        ExperienceStyleOption.ConceptMapFirst -> ExperienceStyle.ConceptMapFirst
        ExperienceStyleOption.QuizFirst -> ExperienceStyle.QuizFirst
        ExperienceStyleOption.CaseStudy -> ExperienceStyle.CaseStudy
    }

private fun GenerationAgentStatus.toUiStatus(): AgentStatus =
    when (this) {
        GenerationAgentStatus.Waiting -> AgentStatus.Waiting
        GenerationAgentStatus.Active -> AgentStatus.Active
        GenerationAgentStatus.Completed -> AgentStatus.Completed
    }

fun AgentTraceEvent.toUiModel(): TraceEventUiModel =
    TraceEventUiModel(
        id = id,
        agentId = agentId,
        type = type,
        title = title,
        message = message,
        timestamp = timestamp,
        payload = payload,
        elapsedSeconds = elapsedSeconds,
    )
