package com.devscion.chapterstage.domain.model

data class GenerationSettings(
    val audienceLevel: AudienceLevel,
    val experienceStyle: ExperienceStyle,
    val targetScreenCount: Int,
    val autoBrainstorm: Boolean,
)
