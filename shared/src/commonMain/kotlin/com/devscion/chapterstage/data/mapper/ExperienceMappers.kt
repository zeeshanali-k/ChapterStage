package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.ExperienceMetadataResponse
import com.devscion.chapterstage.domain.model.ExperienceMetadata

fun ExperienceMetadataResponse.toDomain(): ExperienceMetadata =
    ExperienceMetadata(
        id = experienceId,
        title = title ?: "Chapter experience",
        publicUrl = publicUrl,
        status = status.lowercase(),
    )
