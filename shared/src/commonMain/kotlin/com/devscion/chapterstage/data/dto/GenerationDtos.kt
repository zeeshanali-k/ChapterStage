package com.devscion.chapterstage.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class AudienceLevelDto {
    @SerialName("beginner")
    Beginner,

    @SerialName("intermediate")
    Intermediate,

    @SerialName("expert")
    Expert,
}

@Serializable
enum class ExperienceStyleDto {
    @SerialName("visual_story")
    VisualStory,

    @SerialName("lecture_mode")
    LectureMode,

    @SerialName("concept_map_first")
    ConceptMapFirst,

    @SerialName("quiz_first")
    QuizFirst,

    @SerialName("case_study")
    CaseStudy,
}

@Serializable
data class StartGenerationJobRequest(
    @SerialName("chapter_id") val chapterId: String,
    @SerialName("audience_level") val audienceLevel: AudienceLevelDto,
    @SerialName("experience_style") val experienceStyle: ExperienceStyleDto,
    @SerialName("target_screen_count") val targetScreenCount: Int,
    @SerialName("enable_auto_brainstorm") val enableAutoBrainstorm: Boolean = true,
)

@Serializable
data class GenerationJobStartResponse(
    @SerialName("job_id") val jobId: String,
    @SerialName("chapter_id") val chapterId: String,
    val status: String,
    @SerialName("status_url") val statusUrl: String,
    @SerialName("events_url") val eventsUrl: String,
)

@Serializable
data class GenerationJobResponse(
    @SerialName("job_id") val jobId: String,
    @SerialName("chapter_id") val chapterId: String,
    val status: String,
    val progress: Double,
    @SerialName("current_step") val currentStep: String? = null,
    @SerialName("band_room_id") val bandRoomId: String? = null,
    @SerialName("experience_id") val experienceId: String? = null,
    @SerialName("public_url") val publicUrl: String? = null,
    val error: ApiErrorBody? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class RecentGenerationJobsResponse(
    val jobs: List<RecentGenerationJobDto> = emptyList(),
    val limit: Int = 20,
    val offset: Int = 0,
)

@Serializable
data class RecentGenerationJobDto(
    @SerialName("job_id") val jobId: String,
    @SerialName("chapter_id") val chapterId: String? = null,
    val title: String? = null,
    val book: String? = null,
    val status: String = "unknown",
    val progress: Double = 0.0,
    @SerialName("current_step") val currentStep: String? = null,
    @SerialName("band_room_id") val bandRoomId: String? = null,
    @SerialName("experience_id") val experienceId: String? = null,
    @SerialName("public_url") val publicUrl: String? = null,
    val error: ApiErrorBody? = null,
    val style: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class GenerationStreamEventDto(
    val id: String? = null,
    @SerialName("job_id") val jobId: String? = null,
    @SerialName("chapter_id") val chapterId: String? = null,
    val status: String? = null,
    val progress: Double? = null,
    @SerialName("current_step") val currentStep: String? = null,
    @SerialName("active_agent_id") val activeAgentId: String? = null,
    @SerialName("agent_id") val agentId: String? = null,
    @SerialName("agent_name") val agentName: String? = null,
    @SerialName("event_type") val eventType: String? = null,
    val title: String? = null,
    val message: String? = null,
    val payload: JsonElement? = null,
    @SerialName("agent_statuses") val agentStatuses: Map<String, String> = emptyMap(),
    @SerialName("band_room_id") val bandRoomId: String? = null,
    @SerialName("experience_id") val experienceId: String? = null,
    @SerialName("public_url") val publicUrl: String? = null,
    @SerialName("elapsed_seconds") val elapsedSeconds: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val error: ApiErrorBody? = null,
)
