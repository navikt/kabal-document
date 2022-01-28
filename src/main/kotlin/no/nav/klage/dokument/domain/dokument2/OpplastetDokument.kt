package no.nav.klage.dokument.domain.dokument2

import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "opplastet_dokument", schema = "dokument")
@DiscriminatorColumn(name = "behandling_type")
abstract class Dokument(
    @Id
    var id: UUID,
    var type: String,
    var mellomlagerId: String,
    var opplastet: LocalDateTime,
    var size: Long,
    var name: String,
    var smartEditorId: UUID? = null,
    var eksternReferanse: String,
    var dokumentType: String?,
    var created: LocalDateTime = LocalDateTime.now(),
    var modified: LocalDateTime = LocalDateTime.now(),
    var ferdigstilt: LocalDateTime? = null,
) {
    companion object {
        const val HOVED_DOKUMENT = "hoved"
        const val VEDLEGG = "vedlegg"
    }

    fun ferdigstillHvisIkkeAlleredeFerdigstilt(tidspunkt: LocalDateTime) {
        if (ferdigstilt != null) {
            ferdigstilt = tidspunkt
            modified = tidspunkt
        }
    }
}

@Entity
@DiscriminatorValue(Dokument.HOVED_DOKUMENT)
open class HovedDokument(
    id: UUID = UUID.randomUUID(),
    mellomlagerId: String,
    opplastet: LocalDateTime,
    size: Long,
    name: String,
    smartEditorId: UUID? = null,
    eksternReferanse: String,
    dokumentType: String?,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    var vedlegg: List<Vedlegg> = emptyList(),
    var dokumentEnhetId: UUID? = null,
) : Dokument(
    id = id,
    type = HOVED_DOKUMENT,
    mellomlagerId = mellomlagerId,
    opplastet = opplastet,
    size = size,
    name = name,
    smartEditorId = smartEditorId,
    eksternReferanse = eksternReferanse,
    dokumentType = dokumentType,
) {
    fun toVedlegg(): Vedlegg =
        Vedlegg(
            id = id,
            mellomlagerId = mellomlagerId,
            opplastet = opplastet,
            size = size,
            name = name,
            smartEditorId = smartEditorId,
            eksternReferanse = eksternReferanse,
            dokumentType = dokumentType,
        )

    fun ferdigstillHvisIkkeAlleredeFerdigstilt() {
        val naa = LocalDateTime.now()
        super.ferdigstillHvisIkkeAlleredeFerdigstilt(naa)
        vedlegg.forEach { it.ferdigstillHvisIkkeAlleredeFerdigstilt(naa) }
    }
}

@Entity
@DiscriminatorValue(Dokument.VEDLEGG)
open class Vedlegg(
    id: UUID = UUID.randomUUID(),
    mellomlagerId: String,
    opplastet: LocalDateTime,
    size: Long,
    name: String,
    smartEditorId: UUID? = null,
    eksternReferanse: String,
    dokumentType: String?,
) : Dokument(
    id = id,
    type = VEDLEGG,
    mellomlagerId = mellomlagerId,
    opplastet = opplastet,
    size = size,
    name = name,
    smartEditorId = smartEditorId,
    eksternReferanse = eksternReferanse,
    dokumentType = dokumentType
) {
    fun toHovedDokument(): HovedDokument =
        HovedDokument(
            id = id,
            mellomlagerId = mellomlagerId,
            opplastet = opplastet,
            size = size,
            name = name,
            smartEditorId = smartEditorId,
            eksternReferanse = eksternReferanse,
            dokumentType = dokumentType,
            vedlegg = emptyList(),
        )

}
