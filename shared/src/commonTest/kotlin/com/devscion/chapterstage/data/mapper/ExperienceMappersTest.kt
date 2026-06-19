package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.ExperienceMetadataResponse
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

class ExperienceMappersTest {
    @Test
    fun `given metadata response without title when mapped then safe defaults are used`() {
        val response = ExperienceMetadataResponse(
            experienceId = "experience-1",
            jobId = "job-1",
            publicUrl = "https://chapterstage.app/c/experience-1",
            createdAt = "2026-06-17T00:00:00",
        )

        val metadata = response.toDomain()

        assertEquals("experience-1", metadata.id)
        assertEquals("Chapter experience", metadata.title)
        assertEquals("https://chapterstage.app/c/experience-1", metadata.publicUrl)
        assertEquals("ready", metadata.status)
    }

    @Test
    fun `given metadata response with chapter title when mapped then title is derived`() {
        val response = ExperienceMetadataResponse(
            experienceId = "experience-1",
            jobId = "job-1",
            publicUrl = "https://chapterstage.app/c/experience-1",
            metadata = buildJsonObject {
                put("chapter_title", "The Cell Cycle")
                put("book_title", "Biology 101")
            },
            createdAt = "2026-06-17T00:00:00",
        )

        val metadata = response.toDomain()

        assertEquals("The Cell Cycle", metadata.title)
    }
}
