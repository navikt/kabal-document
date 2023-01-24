package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Entity
@Table(name = "brevmottakerdist", schema = "document")
@DynamicUpdate
class BrevMottakerDistribusjon(
    @Id
    val id: UUID = UUID.randomUUID(),
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "brev_mottaker_id", referencedColumnName = "id", nullable = false)
    val brevMottaker: BrevMottaker,
    @Column(name = "opplastet_dokument_id")
    val opplastetDokumentId: UUID,
    @Column(name = "journalpost_id")
    var journalpostId: String? = null,
    @Column(name = "ferdigstilt_i_joark")
    var ferdigstiltIJoark: LocalDateTime? = null,
    @Column(name = "dokdist_referanse")
    var dokdistReferanse: UUID? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BrevMottakerDistribusjon

        if (id != other.id) return false
        if (opplastetDokumentId != other.opplastetDokumentId) return false
        if (journalpostId != other.journalpostId) return false
        if (ferdigstiltIJoark?.truncatedTo(ChronoUnit.MILLIS) != other.ferdigstiltIJoark?.truncatedTo(ChronoUnit.MILLIS)) return false
        if (dokdistReferanse != other.dokdistReferanse) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}