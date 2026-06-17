package com.devscion.chapterstage.domain.model

data class AgentTraceEvent(
    val id: String,
    val agentId: String,
    val agentName: String,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val payload: String? = null,
)
