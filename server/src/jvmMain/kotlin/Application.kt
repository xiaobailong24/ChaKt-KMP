import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.lastOrNull
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
    val chatServer = aiService.startChat(listOf())

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText("Gemini Server in illegal state as ${cause.message}")
        }
    }

    routing {
        get("/") {
            val completeText = StringBuilder()
            chatServer.sendMessageStream("写一段中国龙年新年祝福语")
                .map { it.text ?: "" }
                .onEach { completeText.append(it) }
                .catch { call.respondText(it.toString()) }
                .lastOrNull()?.let {
                    call.respondText(completeText.toString())
                } ?: call.respondText("No response")
        }

        get("/error") {
            throw IllegalStateException("Illegal State Exception")
        }
    }
}
