package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "opplastetdokument", schema = "document")
open class OpplastetDokument(
    @Id
    open val id: UUID = UUID.randomUUID(),
    @Column(name = "mellomlager_id")
    val mellomlagerId: String,
    @Column(name = "opplastet")
    val opplastet: LocalDateTime,
    @Column(name = "size")
    val size: Long,
    @Column(name = "name")
    val name: String,
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    var type: OpplastetDokumentType
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

    enum class OpplastetDokumentType {
        HOVEDDOKUMENT,
        VEDLEGG,
    }
}