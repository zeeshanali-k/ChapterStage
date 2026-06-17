package com.devscion.chapterstage.data.mapper

import com.devscion.chapterstage.data.remote.ServerSentEvent
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenerationStreamMappersTest {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `given sparse stream event when mapped then job keeps derived trace state`() {
        val event = ServerSentEvent(
            event = "agent_update",
            data = """
                {
                  "job_id": "job-1",
                  "chapter_id": "chapter-1",
                  "status": "running",
                  "progress": 35,
                  "active_agent_id": "structure",
                  "title": "Mapping the chapter",
                  "message": "Reading sections."
                }
            """.trimIndent(),
        )

        val dto = event.toGenerationStreamEvent(json)
        val trace = dto?.toTraceEvent(streamEventName = event.event, index = 0)
        val job = dto?.toDomainJob(
            jobId = "job-1",
            previous = null,
            traceEvents = listOfNotNull(trace),
        )

        assertEquals("running", job?.status)
        assertEquals(35, job?.progress)
        assertEquals("structure", job?.activeAgentId)
        assertEquals("Mapping the chapter", trace?.title)
        assertEquals("Reading sections.", trace?.message)
    }

    @Test
    fun `given invalid stream data when mapped then null is returned`() {
        val event = ServerSentEvent(event = "progress", data = "not-json")

        assertNull(event.toGenerationStreamEvent(json))
    }

    @Test
    fun `given backend progress event when mapped then fractional progress becomes percent`() {
        val event = ServerSentEvent(
            event = "job_progress",
            data = """
                {
                  "status": "creating_band_room",
                  "progress": 0.18,
                  "message": "Creating Band chapter room."
                }
            """.trimIndent(),
        )

        val dto = event.toGenerationStreamEvent(json)
        val trace = dto?.toTraceEvent(streamEventName = event.event, index = 0)
        val job = dto?.toDomainJob(
            jobId = "job-1",
            previous = null,
            traceEvents = listOfNotNull(trace),
        )

        assertEquals("creating_band_room", job?.status)
        assertEquals(18, job?.progress)
        assertEquals("Creating Band chapter room.", trace?.message)
    }

    @Test
    fun `given backend agent event payload object when mapped then payload is preserved as text`() {
        val event = ServerSentEvent(
            event = "agent_message",
            data = """
                {
                  "agent_name": "structure",
                  "title": "structure to brainstorm",
                  "message": "Delivered knowledge_pack envelope.",
                  "payload": {
                    "from": "structure",
                    "to": "brainstorm",
                    "kind": "knowledge_pack"
                  }
                }
            """.trimIndent(),
        )

        val dto = event.toGenerationStreamEvent(json)
        val trace = dto?.toTraceEvent(streamEventName = event.event, index = 0)

        assertTrue(trace?.payload?.contains("\"kind\":\"knowledge_pack\"") == true)
    }
}
