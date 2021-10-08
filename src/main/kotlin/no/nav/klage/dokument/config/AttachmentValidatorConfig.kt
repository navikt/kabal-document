package no.nav.klage.dokument.config

import no.nav.klage.dokument.clients.clamav.ClamAvClient
import no.nav.klage.dokument.util.AttachmentValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.unit.DataSize

@Configuration
class AttachmentValidatorConfig(private val clamAvClient: ClamAvClient) {

    @Value("\${maxAttachmentSize}")
    private lateinit var maxAttachmentSizeAsString: String

    @Bean
    fun attachmentValidator() = AttachmentValidator(
        clamAvClient,
        DataSize.parse(maxAttachmentSizeAsString)
    )
}