package no.nav.klage.dokument.domain.dokument

import jakarta.persistence.*
import jakarta.persistence.CascadeType
import jakarta.persistence.Table
import no.nav.klage.kodeverk.DokumentType
import org.hibernate.annotations.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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
    @BatchSize(size = 100)
    var vedlegg: List<OpplastetVedlegg> = emptyList(),
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
    @Column(name = "modified")
    var modified: LocalDateTime = LocalDateTime.now(),
    @Column(name = "should_be_distributed")
    var shouldBeDistributed: Boolean = true
) {

    fun isAvsluttet() = avsluttet != null

    fun isDistributedTo(brevMottaker: BrevMottaker): Boolean =
        findBrevMottakerDistribusjon(brevMottaker)?.dokdistReferanse != null

    fun isJournalfoertFor(brevMottaker: BrevMottaker): Boolean =
        findBrevMottakerDistribusjon(brevMottaker)?.journalpostId != null && findBrevMottakerDistribusjon(brevMottaker)?.ferdigstiltIJoark != null

    //Trengs dette?
    fun isProcessedForAll(): Boolean {
        return if (shouldBeDistributed) {
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

    fun harHovedDokument(): Boolean = hovedDokument != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DokumentEnhet

        if (id != other.id) return false
        if (journalfoeringData != other.journalfoeringData) return false
        if (brevMottakere != other.brevMottakere) return false
        if (hovedDokument != other.hovedDokument) return false
        //Necessary due to Hibernate list comparison bug
        if (vedlegg != other.vedlegg && other.vedlegg != vedlegg) return false
        if (brevMottakerDistribusjoner != other.brevMottakerDistribusjoner) return false
        if (avsluttet?.truncatedTo(ChronoUnit.MILLIS) != other.avsluttet?.truncatedTo(ChronoUnit.MILLIS)) return false
        if (modified.truncatedTo(ChronoUnit.MILLIS) != other.modified.truncatedTo(ChronoUnit.MILLIS)) return false
        if (shouldBeDistributed != other.shouldBeDistributed) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}