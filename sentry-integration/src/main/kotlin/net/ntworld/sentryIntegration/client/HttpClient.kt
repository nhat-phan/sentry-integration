package net.ntworld.sentryIntegration.client

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import net.ntworld.sentryIntegration.entity.Connection
import com.github.kittinunf.result.Result as FuelResult

internal class HttpClient(private val connection: Connection) {
    internal val baseUrl by lazy {
        if (connection.url.endsWith('/')) {
            connection.url.substring(0, connection.url.length - 1)
        } else {
            connection.url
        }
    }

    fun get(endpoint: String): String {
        val request = buildEndpointUrl(endpoint).httpGet()
        request.set("Authorization", "Bearer ${connection.token}")
        val (_, response, result) = request.responseString()

        return handleResultString(response, result)
    }

    fun put(endpoint: String, body: String): String {
        val request = buildEndpointUrl(endpoint).httpPut()
        request.set("Authorization", "Bearer ${connection.token}")
        request.set("Content-Type", "application/json")
        request.body(body)
        val (_, response, result) = request.responseString()

        return handleResultString(response, result)
    }

    private fun buildEndpointUrl(endpoint: String): String {
        return "$baseUrl/api/0$endpoint"
    }

    private fun handleResultString(response: Response, result: FuelResult<String, FuelError>): String {
        when (result) {
            is FuelResult.Failure -> {
                throw result.getException()
            }
            is FuelResult.Success -> {
                return result.get()
            }
        }
    }
}