package net.ntworld.sentryIntegration.client

import net.ntworld.sentryIntegration.entity.Connection
import org.junit.Test
import kotlin.test.assertEquals

class HttpClientTest {
    @Test
    public fun testBaseUrl() {
        val dataset = mapOf(
            "" to "",
            "/" to "",
            "test/" to "test",
            "https://sentry.io" to "https://sentry.io",
            "https://sentry.io/" to "https://sentry.io"
        )

        dataset.forEach { (input, expected) ->
            val client = HttpClient(Connection("", input, ""))
            assertEquals(expected, client.baseUrl)
        }
    }
}