package com.devscion.chapterstage.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExperienceMetadataResponse(
    @SerialName("experience_id") val experienceId: String,
    val title: String? = null,
    @SerialName("public_url") val publicUrl: String,
    val status: String = "ready",
)
