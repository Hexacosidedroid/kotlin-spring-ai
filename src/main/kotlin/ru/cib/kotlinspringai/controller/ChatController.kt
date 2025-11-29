package ru.cib.kotlinspringai.controller

import jakarta.servlet.http.HttpSession
import kotlinx.serialization.json.Json
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import ru.cib.kotlinspringai.dto.FileCreation
import ru.cib.kotlinspringai.dto.SearchDuckDuckGo
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.cib.kotlinspringai.config.ToolConfig
import ru.cib.kotlinspringai.service.ChatService

@RestController
class ChatController(
    private val chatService: ChatService,
) {

    @PostMapping("/createFile")
    fun create(@RequestParam request: String, session: HttpSession): String? {
        return chatService.chatCreate("/Users/slava_ivanov_saikyo/kotlin-spring-ai", request, session.id)
    }

    @GetMapping("/search={request}")
    fun search(@PathVariable request: String, session: HttpSession): String? {
        return chatService.chatSearch(request, session.id)
    }

    @GetMapping("/ask={question}")
    fun ask(@PathVariable question: String, session: HttpSession): String? {
        return chatService.chatAsk(question, session.id)
    }


}