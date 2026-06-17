package com.devscion.chapterstage.data.repository

import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.ExperienceMetadata
import com.devscion.chapterstage.domain.model.GenerationAgentStatus
import com.devscion.chapterstage.domain.model.GenerationJob
import com.devscion.chapterstage.domain.model.GenerationSettings
import com.devscion.chapterstage.domain.model.RecentGenerationJob
import com.devscion.chapterstage.domain.repository.GenerationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class MockGenerationRepository : GenerationRepository {
    override suspend fun getRecentJobs(): Result<List<RecentGenerationJob>> =
        Result.success(MockWorkflowData.recentJobs)

    override suspend fun startGeneration(chapterId: String, settings: GenerationSettings): Result<GenerationJob> =
        Result.success(snapshot(chapterId = chapterId, revealedEventCount = 0, elapsedSeconds = 0))

    override fun observeGeneration(jobId: String): Flow<GenerationJob> = flow {
        for (eventCount in 0..MockWorkflowData.traceEvents.size) {
            if (eventCount > 0) delay(720)
            emit(
                snapshot(
                    chapterId = MockWorkflowData.ChapterId,
                    revealedEventCount = eventCount,
                    elapsedSeconds = (eventCount * 2).coerceAtMost(22),
                ),
            )
        }
    }

    override suspend fun getTrace(jobId: String): Result<List<AgentTraceEvent>> =
        Result.success(MockWorkflowData.traceEvents)

    override suspend fun getExperienceMetadata(experienceId: String): Result<ExperienceMetadata> =
        Result.success(
            ExperienceMetadata(
                id = experienceId,
                title = "Photosynthesis",
                publicUrl = MockWorkflowData.PublicUrl,
                status = "ready",
            ),
        )

    private fun snapshot(
        chapterId: String,
        revealedEventCount: Int,
        elapsedSeconds: Int,
    ): GenerationJob {
        val visibleEvents = MockWorkflowData.traceEvents.take(revealedEventCount)
        val isComplete = revealedEventCount >= MockWorkflowData.traceEvents.size
        val activeAgentId = activeAgentId(revealedEventCount = revealedEventCount, isComplete = isComplete)
        val completedAgents = completedAgentIds(revealedEventCount = revealedEventCount, isComplete = isComplete)
        val allAgentIds = setOf("coordinator", "structure", "pedagogy", "brainstorm", "visual", "verifier")
        val statuses = allAgentIds.associateWith { agentId ->
            when {
                agentId in completedAgents -> GenerationAgentStatus.Completed
                agentId == activeAgentId -> GenerationAgentStatus.Active
                else -> GenerationAgentStatus.Waiting
            }
        }

        return GenerationJob(
            id = MockWorkflowData.JobId,
            chapterId = chapterId,
            status = if (isComplete) "completed" else "running",
            progress = if (isComplete) {
                100
            } else {
                (revealedEventCount * 100 / MockWorkflowData.traceEvents.size).coerceAtLeast(6)
            },
            currentStep = visibleEvents.lastOrNull()?.title ?: "Queued",
            activeAgentId = activeAgentId,
            agentStatuses = statuses,
            events = visibleEvents,
            elapsedSeconds = elapsedSeconds,
            bandRoomId = "band-room-px-4471",
            experienceId = if (isComplete) "experience-photosynthesis" else null,
            publicUrl = if (isComplete) MockWorkflowData.PublicUrl else null,
        )
    }

    private fun activeAgentId(revealedEventCount: Int, isComplete: Boolean): String? =
        when {
            isComplete -> null
            revealedEventCount <= 1 -> "coordinator"
            revealedEventCount <= 3 -> "structure"
            revealedEventCount <= 4 -> "pedagogy"
            revealedEventCount <= 8 -> "brainstorm"
            revealedEventCount <= 10 -> "visual"
            else -> "verifier"
        }

    private fun completedAgentIds(revealedEventCount: Int, isComplete: Boolean): Set<String> =
        when {
            isComplete -> setOf("coordinator", "structure", "pedagogy", "brainstorm", "visual", "verifier")
            revealedEventCount <= 1 -> emptySet()
            revealedEventCount <= 3 -> setOf("coordinator")
            revealedEventCount <= 4 -> setOf("coordinator", "structure")
            revealedEventCount <= 8 -> setOf("coordinator", "structure", "pedagogy")
            revealedEventCount <= 10 -> setOf("coordinator", "structure", "pedagogy", "brainstorm")
            else -> setOf("coordinator", "structure", "pedagogy", "brainstorm", "visual")
        }
}
