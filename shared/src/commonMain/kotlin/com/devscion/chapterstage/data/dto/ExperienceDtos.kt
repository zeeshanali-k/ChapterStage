package com.devscion.chapterstage.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ExperienceMetadataResponse(
    @SerialName("experience_id") val experienceId: String,
    @SerialName("job_id") val jobId: String,
    @SerialName("public_url") val publicUrl: String,
    val metadata: JsonObject = JsonObject(emptyMap()),
    @SerialName("created_at") val createdAt: String,
)
