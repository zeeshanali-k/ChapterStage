package com.devscion.chapterstage.domain.model

data class RecentGenerationJob(
    val id: String,
    val title: String,
    val book: String,
    val status: String,
    val style: String,
    val updatedAt: String,
    val progress: Int = 0,
    val currentStep: String? = null,
    val experienceId: String? = null,
    val publicUrl: String? = null,
    val errorMessage: String? = null,
)
