package no.nav.klage.dokument.config

import org.springframework.context.annotation.Configuration
import org.springframework.resilience.annotation.EnableResilientMethods

@Configuration
@EnableResilientMethods
class RetryConfiguration