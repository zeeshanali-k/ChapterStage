package com.devscion.chapterstage.data.remote

data class ServerSentEvent(
    val event: String? = null,
    val data: String,
    val id: String? = null,
)

class ServerSentEventParser {
    private var event: String? = null
    private var id: String? = null
    private val dataLines = mutableListOf<String>()

    fun accept(line: String): ServerSentEvent? {
        if (line.isBlank()) {
            return flush()
        }

        if (line.startsWith(":")) return null

        val separatorIndex = line.indexOf(':')
        val field = if (separatorIndex >= 0) line.substring(0, separatorIndex) else line
        val rawValue = if (separatorIndex >= 0) line.substring(separatorIndex + 1) else ""
        val value = rawValue.removePrefix(" ")

        when (field) {
            "event" -> event = value
            "data" -> dataLines += value
            "id" -> id = value
        }

        return null
    }

    fun flush(): ServerSentEvent? {
        if (dataLines.isEmpty()) {
            event = null
            id = null
            return null
        }

        val result = ServerSentEvent(
            event = event,
            data = dataLines.joinToString(separator = "\n"),
            id = id,
        )
        event = null
        id = null
        dataLines.clear()
        return result
    }
}
