package no.nav.klage.dokument.domain.dokument

import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.DokumentTypeConverter
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "dokumentenhet", schema = "document")
open class DokumentEnhet(
    @Id
    open val id: UUID = UUID.randomUUID(),
    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "navIdent", column = Column(name = "eier"))
        ]
    )
    val eier: SaksbehandlerIdent,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "journalfoering_id")
    val journalfoeringData: JournalfoeringData,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "dokumentenhet_id", referencedColumnName = "id", nullable = false)
    val brevMottakere: List<BrevMottaker>,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "dokumentenhet_id", referencedColumnName = "id", nullable = false)
    val dokumenter: List<OpplastetDokument>,

    @Column(name = "dokument_type_id")
    @Convert(converter = DokumentTypeConverter::class)
    val dokumentType: DokumentType,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "dokumentenhet_id", referencedColumnName = "id")
    var brevMottakerDistribusjoner: MutableList<BrevMottakerDistribusjon> = mutableListOf(),
    @Column(name = "avsluttet")
    var avsluttet: LocalDateTime? = null,
    @Column(name = "modified")
    val modified: LocalDateTime = LocalDateTime.now()
) {

    fun getHovedDokument() = dokumenter.firstOrNull { it.type == OpplastetDokument.OpplastetDokumentType.HOVEDDOKUMENT }

    fun getVedlegg() = dokumenter.filter { it.type == OpplastetDokument.OpplastetDokumentType.VEDLEGG }

    fun erAvsluttet() = avsluttet != null

    fun erDistribuertTil(brevMottaker: BrevMottaker): Boolean =
        findBrevMottakerDistribusjon(brevMottaker)?.dokdistReferanse != null

    fun erDistribuertTilAlle(): Boolean =
        brevMottakere.all { erDistribuertTil(it) }

    fun findBrevMottakerDistribusjon(brevMottaker: BrevMottaker): BrevMottakerDistribusjon? =
        brevMottakerDistribusjoner.find { it.brevMottakerId == brevMottaker.id }

    fun validateDistribuertTilAlle(): DokumentEnhet =
        if (erDistribuertTilAlle()) {
            this
        } else throw DokumentEnhetNotValidException("DokumentEnhet ikke distribuert til alle brevmottakere")

    fun harHovedDokument(): Boolean = getHovedDokument() != null

    fun getJournalpostIdHovedadressat(): String? =
        brevMottakere.find { it.rolle == Rolle.HOVEDADRESSAT }
            ?.let { findBrevMottakerDistribusjon(it)?.journalpostId?.value }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DokumentEnhet

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}