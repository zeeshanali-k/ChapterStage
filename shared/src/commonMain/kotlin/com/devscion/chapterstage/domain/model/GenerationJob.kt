package com.devscion.chapterstage.domain.model

data class GenerationJob(
    val id: String,
    val chapterId: String,
    val status: String,
    val progress: Int,
    val currentStep: String,
    val activeAgentId: String?,
    val agentStatuses: Map<String, GenerationAgentStatus>,
    val events: List<AgentTraceEvent>,
    val elapsedSeconds: Int,
    val bandRoomId: String? = null,
    val experienceId: String? = null,
    val publicUrl: String? = null,
    val errorMessage: String? = null,
) {
    val isTerminal: Boolean = status == "completed" || status == "cancelled" || status.startsWith("failed")
    val isComplete: Boolean = status == "completed"
}
