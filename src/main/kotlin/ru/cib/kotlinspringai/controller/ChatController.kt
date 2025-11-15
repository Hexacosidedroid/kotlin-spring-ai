package ru.cib.kotlinspringai.controller

import jakarta.servlet.http.HttpSession
import kotlinx.serialization.json.Json
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ResponseEntity
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.cib.kotlinspringai.dto.FileCreation
import java.io.File
import java.util.UUID

@RestController
class ChatController(
    private val chatClient: ChatClient
) {

    @PostMapping("/createFile")
    fun create(@RequestParam request: String, session: HttpSession): String? {
        return chatCreate(request, session.id)
    }

    @GetMapping("/ask={question}")
    fun ask(@PathVariable question: String, session: HttpSession): String? {
        return chatAsk(question, session.id)
    }

    fun chatCreate(request: String, conversationId: String): String? {
        val systemPrompt = """
            *Task:** Generate a JSON object for file creation with the exact following structure:
            ```json
            {
              "place": "string: directory path where file should be created always this one /Users/slava_ivanov_saikyo/kotlin-spring-ai/src/main/resources",
              "file_name": "string: filename without extension",
              "file_extension": "string: file extension including dot (e.g., .txt, .json)",
              "content": "string: content to write into the file"
            }
            ```
            Return me only body of json without ```json ```
        """.trimIndent()
        val result = chatClient.prompt()
            .system(systemPrompt)
            .user(request)
            .advisors {
                it.param(ChatMemory.CONVERSATION_ID, conversationId)
            }
            .call()
            .content()
        val resultJson = result
            ?.replace(Regex("<think>[\\s\\S]*?</think>"), "")
            ?.replace("\n", "")
        val fileCreation = Json.decodeFromString<FileCreation>(resultJson!!)
        File("${fileCreation.place}/${fileCreation.file_name}${fileCreation.file_extension}")
            .writeText(fileCreation.content)
        return "ok"
    }

    fun chatAsk(question: String, conversationId: String): String? {
        val result = chatClient
            .prompt()
            .system("You are my assistant, my name is Slava, he is russian programmer, like astronomy and his job, give me always short answer and call me by name!")
            .user(question)
            .advisors {
                it.param(ChatMemory.CONVERSATION_ID, conversationId)
            }
            .call()
            .content()
        return result
            ?.replace(Regex("<think>[\\s\\S]*?</think>"), "")
            ?.replace("\n", "")
    }
}