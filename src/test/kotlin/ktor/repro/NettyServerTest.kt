package ktor.repro

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NettyServerTest {
    @Test
    fun `netty server responds to a single request`() {
        val server = embeddedServer(Netty, port = 0) {
            routing { get("/health") { call.respondText("OK") } }
        }.start(wait = false)
        val port = runBlocking { server.engine.resolvedConnectors().first().port }
        val client = HttpClient(Java)
        try {
            val status = runBlocking { client.get("http://localhost:$port/health").status }
            assertEquals(HttpStatusCode.OK, status)
        } finally {
            client.close()
            server.stop(100, 1000)
        }
    }
}
