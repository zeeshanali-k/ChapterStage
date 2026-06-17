package com.devscion.chapterstage.data.remote

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ServerSentEventParserTest {
    @Test
    fun `given multiline event, when blank line arrives, then event is emitted`() {
        val parser = ServerSentEventParser()

        assertNull(parser.accept("event: progress"))
        assertNull(parser.accept("id: event-1"))
        assertNull(parser.accept("data: {\"status\":\"running\","))
        assertNull(parser.accept("data: \"progress\":42}"))

        val event = parser.accept("")

        assertEquals("progress", event?.event)
        assertEquals("event-1", event?.id)
        assertEquals("{\"status\":\"running\",\n\"progress\":42}", event?.data)
    }

    @Test
    fun `given comment line, when accepted, then parser ignores it`() {
        val parser = ServerSentEventParser()

        assertNull(parser.accept(": heartbeat"))
        assertNull(parser.flush())
    }
}
