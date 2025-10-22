package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.PartIdTypeConverter

@Embeddable
class PartId(
    @Column(name = "type")
    @Convert(converter = PartIdTypeConverter::class)
    val type: PartIdType,
    @Column(name = "value")
    val value: String,
)
