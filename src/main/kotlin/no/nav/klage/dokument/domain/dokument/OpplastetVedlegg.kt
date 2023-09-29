package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.*

@Entity
@DiscriminatorValue("VEDLEGG")
class OpplastetVedlegg(
    id: UUID = UUID.randomUUID(),
    mellomlagerId: String,
    name: String,
    index: Int,
) : OpplastetDokument(
    id = id,
    mellomlagerId = mellomlagerId,
    name = name,
    type = OpplastetDokumentType.VEDLEGG,
    index = index,
)