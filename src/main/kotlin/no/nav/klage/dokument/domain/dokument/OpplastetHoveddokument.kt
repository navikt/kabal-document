package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.*

@Entity
@DiscriminatorValue("HOVEDDOKUMENT")
class OpplastetHoveddokument(
    id: UUID = UUID.randomUUID(),
    mellomlagerId: String,
    name: String
) : OpplastetDokument(
    id = id,
    mellomlagerId = mellomlagerId,
    name = name,
    type = OpplastetDokumentType.HOVEDDOKUMENT,
    index = 0,
)