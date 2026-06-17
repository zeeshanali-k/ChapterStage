package com.devscion.chapterstage.domain.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenerationJobTest {
    @Test
    fun `given failed status variant when checking terminal then job is terminal`() {
        val job = generationJob(status = "failed_validation")

        assertTrue(job.isTerminal)
        assertFalse(job.isComplete)
    }

    @Test
    fun `given running status when checking terminal then job is not terminal`() {
        val job = generationJob(status = "running")

        assertFalse(job.isTerminal)
        assertFalse(job.isComplete)
    }

    private fun generationJob(status: String): GenerationJob =
        GenerationJob(
            id = "job-1",
            chapterId = "chapter-1",
            status = status,
            progress = 0,
            currentStep = "Running",
            activeAgentId = null,
            agentStatuses = emptyMap(),
            events = emptyList(),
            elapsedSeconds = 0,
        )
}
