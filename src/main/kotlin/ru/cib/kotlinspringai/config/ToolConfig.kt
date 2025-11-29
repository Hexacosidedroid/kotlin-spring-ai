package ru.cib.kotlinspringai.config

import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Configuration
import org.springframework.context.i18n.LocaleContextHolder
import java.time.LocalDateTime

@Configuration
class ToolConfig {

    @Tool(name = "Get the current date and time in the user's timezone")
    fun getCurrentDateTime(): String  {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString()
    }
}