package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

data class OpplastetDokument(
    val id: UUID = UUID.randomUUID(),
    val mellomlagerId: String,
    val opplastet: LocalDateTime,
    val size: Long,
    val name: String,
    val smartEditorId: UUID? = null,
    val dokumentType: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpplastetDokument

        if (id != other.id) return false
        if (mellomlagerId != other.mellomlagerId) return false
        if (opplastet.truncatedTo(ChronoUnit.MILLIS) != other.opplastet.truncatedTo(ChronoUnit.MILLIS)) return false
        if (size != other.size) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}
