package no.nav.klage.dokument.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.text.SimpleDateFormat

@Configuration
class JacksonConfig {

    @Bean
    @Qualifier("ourJacksonObjectMapper")
    fun ourJacksonObjectMapper(): ObjectMapper {
        val jacksonObjectMapper = jacksonObjectMapper()
        jacksonObjectMapper.registerModule(JavaTimeModule())
        jacksonObjectMapper.dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        return jacksonObjectMapper
    }

}