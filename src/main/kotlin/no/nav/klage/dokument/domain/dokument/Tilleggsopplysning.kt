package no.nav.klage.dokument.domain.dokument

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
open class Tilleggsopplysning(
    @Column(name = "key")
    val key: String,
    @Column(name = "value")
    val value: String,
)
