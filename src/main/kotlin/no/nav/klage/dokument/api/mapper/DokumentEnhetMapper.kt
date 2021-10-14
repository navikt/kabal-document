package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.view.*
import no.nav.klage.dokument.domain.dokument.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DokumentEnhetMapper {

    fun getOpplastetFilMetadataView(opplastetDokument: OpplastetDokument?): OpplastetFilMetadataView? =
        opplastetDokument?.let {
            OpplastetFilMetadataView(
                name = it.name,
                size = it.size,
                opplastet = it.opplastet
            )
        }

    fun mapToHovedDokumentEditedView(dokumentEnhet: DokumentEnhet): HovedDokumentEditedView =
        HovedDokumentEditedView(
            dokumentEnhet.modified,
            fileMetadata = getOpplastetFilMetadataView(dokumentEnhet.hovedDokument)
        )

    fun mapToDokumentEnhetFullfoertView(dokumentEnhet: DokumentEnhet): DokumentEnhetFullfoertView =
        DokumentEnhetFullfoertView(dokumentEnhet.erAvsluttet())

    fun mapToByteArray(mellomlagretDokument: MellomlagretDokument): ResponseEntity<ByteArray> =
        ResponseEntity(
            mellomlagretDokument.content,
            HttpHeaders().apply {
                contentType = mellomlagretDokument.contentType
                add("Content-Disposition", "inline; filename=${mellomlagretDokument.title}")
            },
            HttpStatus.OK
        )

    fun mapToDokumentEnhetView(dokumentEnhet: DokumentEnhet): DokumentEnhetView {
        return DokumentEnhetView(
            id = dokumentEnhet.id.toString(),
            eier = dokumentEnhet.eier.navIdent,
            journalfoeringData = mapToJournalfoeringDataView(dokumentEnhet.journalfoeringData),
            brevMottakere = dokumentEnhet.brevMottakere.map { mapToBrevMottakerView(it) },
            hovedDokument = dokumentEnhet.hovedDokument?.let { mapToOpplastetDokumentView(it) },
            vedlegg = dokumentEnhet.vedlegg.map { mapToOpplastetDokumentView(it) },
            brevMottakerDistribusjoner = dokumentEnhet.brevMottakerDistribusjoner.map {
                mapToBrevMottakerDistribusjonView(it)
            },
            avsluttet = dokumentEnhet.avsluttet,
            modified = dokumentEnhet.modified,
        )
    }

    private fun mapToJournalfoeringDataView(journalfoeringData: JournalfoeringData) =
        JournalfoeringDataView(
            sakenGjelder = mapToPartIdView(journalfoeringData.sakenGjelder),
            tema = journalfoeringData.tema.name,
            sakFagsakId = journalfoeringData.sakFagsakId,
            sakFagsystem = journalfoeringData.sakFagsystem?.name,
            kildeReferanse = journalfoeringData.kildeReferanse,
            enhet = journalfoeringData.enhet
        )

    private fun mapToPartIdView(partId: PartId): PartIdView =
        PartIdView(
            type = partId.type.name,
            value = partId.value
        )

    private fun mapToBrevMottakerView(brevMottaker: BrevMottaker): BrevMottakerView =
        BrevMottakerView(
            partId = mapToPartIdView(brevMottaker.partId),
            navn = brevMottaker.navn,
            rolle = brevMottaker.rolle.name
        )

    private fun mapToBrevMottakerDistribusjonView(brevMottakerDistribusjon: BrevMottakerDistribusjon): BrevMottakerDistribusjonView =
        BrevMottakerDistribusjonView(
            brevMottakerId = brevMottakerDistribusjon.brevMottakerId.toString(),
            opplastetDokumentId = brevMottakerDistribusjon.opplastetDokumentId.toString(),
            journalpostId = brevMottakerDistribusjon.journalpostId.value,
            ferdigstiltIJoark = brevMottakerDistribusjon.ferdigstiltIJoark,
            dokdistReferanse = brevMottakerDistribusjon.dokdistReferanse?.toString()
        )

    private fun mapToOpplastetDokumentView(opplastetDokument: OpplastetDokument): OpplastetDokumentView =
        OpplastetDokumentView(
            id = opplastetDokument.id.toString(),
            mellomlagerId = opplastetDokument.mellomlagerId, //TODO Skal vi inkludere denne?
            opplastet = opplastetDokument.opplastet,
            size = opplastetDokument.size,
            name = opplastetDokument.name
        )


}

