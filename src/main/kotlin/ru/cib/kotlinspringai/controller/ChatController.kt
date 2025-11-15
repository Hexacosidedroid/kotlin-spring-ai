package ru.cib.kotlinspringai.controller

import jakarta.servlet.http.HttpSession
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ChatController(
    private val chatClient: ChatClient
) {

    @GetMapping("/ask={question}")
    fun ask(@PathVariable question: String, session: HttpSession): String? {
        return chat(question, session.id)
    }

    fun chat(question: String, conversationId: String): String? {
        val result = chatClient
            .prompt()
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