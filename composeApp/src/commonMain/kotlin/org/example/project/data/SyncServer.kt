package org.example.project.data

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

suspend fun startFileSyncServer(port: Int = 8080) =
    withContext(Dispatchers.Default) {
        val server = embeddedServer(
            CIO,
            port = port,
            module = Application::module,
        )

        val localIP = Network.getLocalIPAddress() ?: "0.0.0.0"
        println("Starting file sync server on http://$localIP:$port...")
        server.start(wait = true)
    }

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            val device = Device(id = Random.nextLong(0L, Long.MAX_VALUE), name = "Device 1")
            call.respond(
                status = HttpStatusCode.OK,
                message = device
            )
        }
    }
}