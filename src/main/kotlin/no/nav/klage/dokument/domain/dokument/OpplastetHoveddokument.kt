package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime
import java.util.*

@Entity
@DiscriminatorValue("HOVEDDOKUMENT")
class OpplastetHoveddokument(
    id: UUID = UUID.randomUUID(),
    mellomlagerId: String,
    opplastet: LocalDateTime,
    size: Long,
    name: String
) : OpplastetDokument(
    id = id,
    mellomlagerId = mellomlagerId,
    opplastet = opplastet,
    size = size,
    name = name,
    type = OpplastetDokumentType.HOVEDDOKUMENT
)