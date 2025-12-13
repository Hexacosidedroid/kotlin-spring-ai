package ru.cib.kotlinspringai.config

import org.springframework.ai.tool.annotation.Tool
import org.springframework.context.annotation.Configuration
import org.springframework.context.i18n.LocaleContextHolder
import java.time.LocalDateTime

class ToolConfig {

    @Tool(name = "Get_the_current_date_and_time_in_the_users_timezone")
    fun getCurrentDateTime(): String  {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString()
    }
}