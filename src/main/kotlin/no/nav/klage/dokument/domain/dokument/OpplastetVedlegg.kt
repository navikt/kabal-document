package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime
import java.util.*

@Entity
@DiscriminatorValue("VEDLEGG")
class OpplastetVedlegg(
    id: UUID = UUID.randomUUID(),
    mellomlagerId: String,
    opplastet: LocalDateTime,
    size: Long,
    name: String,
    index: Int,
) : OpplastetDokument(
    id = id,
    mellomlagerId = mellomlagerId,
    opplastet = opplastet,
    size = size,
    name = name,
    type = OpplastetDokumentType.VEDLEGG,
    index = index,
)