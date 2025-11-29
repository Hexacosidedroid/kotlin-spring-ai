package ru.cib.kotlinspringai.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RestConfig {

    @Bean
    fun restTemplate() = RestTemplateBuilder()
        .defaultHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36")
//        .defaultHeader("Accept", "application/json")
        .build()
}