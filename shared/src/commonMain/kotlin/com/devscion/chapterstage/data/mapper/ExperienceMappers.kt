package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.ExperienceMetadataResponse
import com.devscion.chapterstage.domain.model.ExperienceMetadata
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun ExperienceMetadataResponse.toDomain(): ExperienceMetadata =
    ExperienceMetadata(
        id = experienceId,
        title = metadata.string("title")
            ?: metadata.string("chapter_title")
            ?: "Chapter experience",
        publicUrl = publicUrl,
        status = "ready",
    )

private fun JsonObject.string(key: String): String? =
    (get(key) as? JsonPrimitive)?.takeIf { it.isString }?.content?.takeIf { it.isNotBlank() }
