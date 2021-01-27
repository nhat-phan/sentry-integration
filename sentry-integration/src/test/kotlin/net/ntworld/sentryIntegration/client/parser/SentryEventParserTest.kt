package net.ntworld.sentryIntegration.client

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import net.ntworld.sentryIntegration.client.parser.SentryEventParser
import net.ntworld.sentryIntegration.entity.LinkedProject
import org.junit.Test

class SentryEventParserTest {

    @Test
    fun `test can parse sample 1`() {
        val content = this::class.java.getResource("/responses/event.sample.1.json").readText()
        val parser = SentryEventParser(makeLinkedProject())
        val event = parser.parseEventDetail("123", content)
    }

    private fun makeLinkedProject() = LinkedProject.Empty
}