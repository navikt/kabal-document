package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
import no.nav.klage.dokument.clients.joark.JournalpostType
import no.nav.klage.kodeverk.DokumentType
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "dokumentenhet", schema = "document")
@DynamicUpdate
class DokumentEnhet(
    @Id
    val id: UUID = UUID.randomUUID(),
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "journalfoeringdata_id", referencedColumnName = "id")
    val journalfoeringData: JournalfoeringData,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "dokumentenhet_id", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    val brevMottakere: Set<BrevMottaker>,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "hoveddokument_id", referencedColumnName = "id")
    val hovedDokument: OpplastetHoveddokument? = null,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "dokumentenhet_id", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @OrderBy("index")
    @BatchSize(size = 100)
    var vedlegg: Set<OpplastetVedlegg> = emptySet(),
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "dokumentenhet_id", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @OrderBy("index")
    @BatchSize(size = 100)
    var journalfoerteVedlegg: Set<JournalfoertVedlegg> = emptySet(),
    @Column(name = "dokument_type_id")
    @Convert(converter = DokumentTypeConverter::class)
    val dokumentType: DokumentType,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "dokumentenhet_id", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    var brevMottakerDistribusjoner: Set<BrevMottakerDistribusjon> = emptySet(),
    @Column(name = "avsluttet")
    var avsluttet: LocalDateTime? = null,
    @Column(name = "journalfoerende_saksbehandler_ident")
    val journalfoerendeSaksbehandlerIdent: String,
    @Column(name = "modified")
    var modified: LocalDateTime = LocalDateTime.now(),
) {
    fun shouldBeDistributed(): Boolean {
        return journalfoeringData.journalpostType == JournalpostType.UTGAAENDE
    }

    fun isAvsluttet() = avsluttet != null

    fun isDistributedTo(brevMottaker: BrevMottaker): Boolean =
        findBrevMottakerDistribusjon(brevMottaker)?.dokdistReferanse != null

    fun isJournalfoertFor(brevMottaker: BrevMottaker): Boolean =
        findBrevMottakerDistribusjon(brevMottaker)?.journalpostId != null && findBrevMottakerDistribusjon(brevMottaker)?.ferdigstiltIJoark != null

    //Trengs dette?
    fun isProcessedForAll(): Boolean {
        return if (shouldBeDistributed()) {
            isDistributedToAll()
        } else {
            isJournalfoertForAll()
        }
    }

    fun isDistributedToAll(): Boolean =
        brevMottakere.all { isDistributedTo(it) }

    fun isJournalfoertForAll(): Boolean =
        brevMottakere.all { isJournalfoertFor(it) }

    fun findBrevMottakerDistribusjon(brevMottaker: BrevMottaker): BrevMottakerDistribusjon? =
        brevMottakerDistribusjoner.find { it.brevMottaker == brevMottaker }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DokumentEnhet

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}