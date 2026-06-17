package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.dto.AgentTraceEventDto
import com.devscion.chapterstage.data.dto.GenerationJobResponse
import com.devscion.chapterstage.data.dto.RecentGenerationJobDto
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GenerationMappersTest {
    @Test
    fun `given backend job progress fraction, when mapped, then ui percent is used`() {
        val response = GenerationJobResponse(
            jobId = "job-1",
            chapterId = "chapter-1",
            status = "building_site",
            progress = 0.72,
            currentStep = null,
            createdAt = "2026-06-17T00:00:00",
            updatedAt = "2026-06-17T00:00:01",
        )

        val job = response.toDomain()

        assertEquals(72, job.progress)
        assertEquals("Building Site", job.currentStep)
    }

    @Test
    fun `given backend recent job row, when mapped, then completed job keeps viewer data`() {
        val row = RecentGenerationJobDto(
            jobId = "job-123456789",
            chapterId = "chapter-1",
            status = "completed",
            progress = 1.0,
            currentStep = "Completed",
            experienceId = "exp-1",
            publicUrl = "http://localhost:8000/public/experiences/exp-1/index.html",
            updatedAt = "2026-06-17T00:00:01",
        )

        val job = row.toDomain()

        assertEquals("job-123456789", job.id)
        assertEquals("ready", job.status)
        assertEquals(100, job.progress)
        assertEquals("exp-1", job.experienceId)
        assertEquals("http://localhost:8000/public/experiences/exp-1/index.html", job.publicUrl)
    }

    @Test
    fun `given trace payload object, when mapped, then payload is previewable text`() {
        val dto = AgentTraceEventDto(
            id = "event-1",
            agentName = "structure",
            eventType = "handoff",
            title = "structure to brainstorm",
            message = "Delivered knowledge_pack envelope.",
            createdAt = "2026-06-17T00:00:01",
            payload = buildJsonObject {
                put("from", "structure")
                put("to", "brainstorm")
                put("kind", "knowledge_pack")
            },
        )

        val event = dto.toDomain()

        val payload = assertNotNull(event.payload)
        assertTrue(payload.contains("\"kind\":\"knowledge_pack\""))
    }
}
