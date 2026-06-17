package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded

@Embeddable
class Representant(
    @Embedded
    val partId: PartId,
    @Column(name = "navn")
    val navn: String,
    @Embedded
    val adresse: Adresse?,
)
