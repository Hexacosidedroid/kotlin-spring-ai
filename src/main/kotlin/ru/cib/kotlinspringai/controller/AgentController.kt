package ru.cib.kotlinspringai.controller

import jakarta.servlet.http.HttpSession
import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import ru.cib.kotlinspringai.service.ChatService

@RestController
class AgentController(
    private val chatService: ChatService
) {

    @GetMapping("/request={request}")
    fun request(@PathVariable request: String, session: HttpSession): String? {
        val items = chatService.chatRequest(request, session.id)
        val listOfItems = items?.split(",")
        val listOfResult = mutableListOf<String?>()
        listOfItems?.forEach {
            listOfResult.add(chatService.chatSearch(it, null))
        }
        return listOfResult.toString()
    }
}