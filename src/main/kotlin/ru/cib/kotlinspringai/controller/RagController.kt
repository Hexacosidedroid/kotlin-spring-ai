package ru.cib.kotlinspringai.controller

import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.cib.kotlinspringai.service.RagService

@RestController
class RagController(
    private val ragService: RagService
) {

    @GetMapping("/rag")
    fun loadRag() {
        ragService.loadToVectorDatabase()
    }

    @GetMapping("/askRag={request}")
    fun loadAskRag(@PathVariable request: String, session: HttpSession) {
        ragService.searchVectorDatabase(request, conversationId = session.id)
    }

}