package com.devscion.chapterstage.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AgentTraceResponse(
    @SerialName("job_id") val jobId: String,
    @SerialName("band_room_id") val bandRoomId: String? = null,
    val events: List<AgentTraceEventDto>,
)

@Serializable
data class AgentTraceEventDto(
    val id: String,
    @SerialName("agent_name") val agentName: String? = null,
    @SerialName("agent_id") val agentId: String? = null,
    @SerialName("event_type") val eventType: String,
    val title: String,
    val message: String,
    @SerialName("elapsed_seconds") val elapsedSeconds: Int? = null,
    @SerialName("created_at") val createdAt: String,
    val payload: JsonElement? = null,
)
