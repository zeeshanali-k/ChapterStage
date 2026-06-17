package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.ExperienceMetadataResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class ExperienceMappersTest {
    @Test
    fun `given metadata response without title, when mapped, then safe defaults are used`() {
        val response = ExperienceMetadataResponse(
            experienceId = "experience-1",
            title = null,
            publicUrl = "https://chapterstage.app/c/experience-1",
            status = "READY",
        )

        val metadata = response.toDomain()

        assertEquals("experience-1", metadata.id)
        assertEquals("Chapter experience", metadata.title)
        assertEquals("https://chapterstage.app/c/experience-1", metadata.publicUrl)
        assertEquals("ready", metadata.status)
    }
}
