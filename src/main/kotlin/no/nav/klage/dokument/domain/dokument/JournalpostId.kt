package no.nav.klage.dokument.domain.dokument

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
open class JournalpostId(
    @Column(name = "value")
    val value: String,
)