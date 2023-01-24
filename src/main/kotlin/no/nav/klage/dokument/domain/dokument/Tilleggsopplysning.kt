package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Tilleggsopplysning(
    @Column(name = "key")
    val key: String,
    @Column(name = "value")
    val value: String,
)
