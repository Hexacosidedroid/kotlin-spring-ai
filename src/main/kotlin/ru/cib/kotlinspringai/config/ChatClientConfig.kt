package ru.cib.kotlinspringai.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatClientConfig(
    private val chatClientBuilder: ChatClient.Builder,
    private val chatMemoryRepository: ChatMemoryRepository
) {

    @Bean
    fun chatClient(): ChatClient {
        val messageWindowChatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(10)
            .build()
        val memoryAdvisor = MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
            .build()
        return chatClientBuilder
            .defaultAdvisors(SimpleLoggerAdvisor(), memoryAdvisor)
            .build()
    }

}