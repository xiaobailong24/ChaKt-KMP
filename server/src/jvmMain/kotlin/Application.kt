import dev.shreyaspatil.ai.client.generativeai.type.content
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import server.GenerativeAiService

const val SERVER_PORT = 25100

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val aiService = GenerativeAiService.instance
    val chatServer = aiService.startChat(
        history = listOf(
            content(role = "user") { text("Hello AI.") },
            content(role = "model") { text("Great to meet you. What would you like to know?") },
        ),
    )

    routing {
        get("/") {
            val completeText = StringBuilder()

            chatServer.sendMessageStream("Hello AI.")
                .map { it.text ?: "" }
                .onEach { completeText.append(it) }
                .catch { call.respondText(it.toString()) }
                .collect { call.respondText(it) }
        }
    }
}
