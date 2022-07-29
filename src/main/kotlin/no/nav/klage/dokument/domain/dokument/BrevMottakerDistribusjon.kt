package no.nav.klage.dokument.domain.dokument

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "brevmottakerdist", schema = "document")
open class BrevMottakerDistribusjon(
    @Id
    open val id: UUID = UUID.randomUUID(),
    @Column(name = "brev_mottaker_id")
    val brevMottakerId: UUID,
    @Column(name = "opplastet_dokument_id")
    val opplastetDokumentId: UUID,
    @Column(name = "opplastet_dokument_id")
    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "value", column = Column(name = "journalpost_id"))
        ]
    )
    val journalpostId: JournalpostId,
    @Column(name = "ferdigstilt_i_joark")
    var ferdigstiltIJoark: LocalDateTime? = null,
    @Column(name = "dokdist_referanse")
    var dokdistReferanse: UUID? = null,
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "dokumentenhet_id")
    var dokumentEnhet: DokumentEnhet,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BrevMottakerDistribusjon

        if (id != other.id) return false
        if (brevMottakerId != other.brevMottakerId) return false
        if (opplastetDokumentId != other.opplastetDokumentId) return false
        if (journalpostId != other.journalpostId) return false
        if (ferdigstiltIJoark?.truncatedTo(ChronoUnit.MILLIS) != other.ferdigstiltIJoark?.truncatedTo(ChronoUnit.MILLIS)) return false
        if (dokdistReferanse != other.dokdistReferanse) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}