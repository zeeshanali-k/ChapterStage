package com.devscion.chapterstage.data.repository

import com.devscion.chapterstage.domain.model.AgentTraceEvent
import com.devscion.chapterstage.domain.model.RecentGenerationJob

internal object MockWorkflowData {
    const val ChapterId = "chapter-photosynthesis"
    const val JobId = "job-px-4471"
    const val BookId = "book-living-systems"
    const val PublicUrl = "chapterstage.app/c/photosynthesis"

    val traceEvents: List<AgentTraceEvent> = listOf(
        AgentTraceEvent(
            id = "event-1",
            agentId = "coordinator",
            agentName = "Coordinator",
            type = "Delegated",
            title = "Briefed the Band",
            message = "Routed the chapter to five specialists.",
            timestamp = "00:01",
            payload = "job#PX-4471 - style=Visual Story - level=Intermediate",
        ),
        AgentTraceEvent(
            id = "event-2",
            agentId = "structure",
            agentName = "Structure",
            type = "Analyzed",
            title = "Mapping the chapter",
            message = "Reading sections, figures and key terms.",
            timestamp = "00:03",
        ),
        AgentTraceEvent(
            id = "event-3",
            agentId = "structure",
            agentName = "Structure",
            type = "Generated",
            title = "Chapter map ready",
            message = "6 core concepts, 3 sub-topics, 11 key terms.",
            timestamp = "00:05",
            payload = "light reactions - Calvin cycle - chlorophyll - ATP",
        ),
        AgentTraceEvent(
            id = "event-4",
            agentId = "pedagogy",
            agentName = "Pedagogy",
            type = "Analyzed",
            title = "Finding confusions",
            message = "Flagged 4 misconceptions learners commonly hold.",
            timestamp = "00:07",
            payload = "plants eat soil - photosynthesis equals breathing",
        ),
        AgentTraceEvent(
            id = "event-5",
            agentId = "brainstorm",
            agentName = "Auto-Brainstorm",
            type = "Brainstormed",
            title = "Five format concepts",
            message = "Visual Story, Lecture, Map-first, Quiz-first, Case.",
            timestamp = "00:09",
        ),
        AgentTraceEvent(
            id = "event-6",
            agentId = "brainstorm",
            agentName = "Auto-Brainstorm",
            type = "Scored",
            title = "Scored concepts",
            message = "Visual Story leads on clarity and engagement.",
            timestamp = "00:11",
            payload = "visual 0.91 - case 0.78 - map 0.74 - quiz 0.70",
        ),
        AgentTraceEvent(
            id = "event-7",
            agentId = "brainstorm",
            agentName = "Auto-Brainstorm",
            type = "Rejected",
            title = "Dropped Quiz-first",
            message = "Too abstract before the core idea lands.",
            timestamp = "00:12",
        ),
        AgentTraceEvent(
            id = "event-8",
            agentId = "brainstorm",
            agentName = "Auto-Brainstorm",
            type = "Selected",
            title = "Selected Visual Story",
            message = "Handing the blueprint to Visual Builder.",
            timestamp = "00:13",
        ),
        AgentTraceEvent(
            id = "event-9",
            agentId = "visual",
            agentName = "Visual Builder",
            type = "Generated",
            title = "Building scenes",
            message = "Composing interactive scenes with diagrams.",
            timestamp = "00:15",
        ),
        AgentTraceEvent(
            id = "event-10",
            agentId = "visual",
            agentName = "Visual Builder",
            type = "Generated",
            title = "8 scenes built",
            message = "Hook, chapter map, four concepts, quiz, recap.",
            timestamp = "00:18",
            payload = "site 86 KB - no external scripts - 360px OK",
        ),
        AgentTraceEvent(
            id = "event-11",
            agentId = "verifier",
            agentName = "Verifier",
            type = "Verified",
            title = "Faithfulness and safety",
            message = "Cross-checked every claim against the source.",
            timestamp = "00:20",
            payload = "faithfulness 0.96 - safety PASS",
        ),
        AgentTraceEvent(
            id = "event-12",
            agentId = "coordinator",
            agentName = "Coordinator",
            type = "Published",
            title = "Experience published",
            message = "Interactive chapter is live and shareable.",
            timestamp = "00:22",
        ),
    )

    val recentJobs: List<RecentGenerationJob> = listOf(
        RecentGenerationJob(
            id = "recent-photosynthesis",
            title = "Photosynthesis",
            book = "Living Systems - Ch.4",
            status = "ready",
            style = "Visual Story",
            updatedAt = "2m ago",
        ),
        RecentGenerationJob(
            id = "recent-supply-demand",
            title = "Supply and Demand",
            book = "Foundations of Economics - Ch.2",
            status = "generating",
            style = "Case Study",
            updatedAt = "now",
        ),
        RecentGenerationJob(
            id = "recent-french-revolution",
            title = "The French Revolution",
            book = "A Short History - Ch.9",
            status = "ready",
            style = "Concept Map",
            updatedAt = "1h ago",
        ),
    )
}
