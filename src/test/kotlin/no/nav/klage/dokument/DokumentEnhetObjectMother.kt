package no.nav.klage.dokument

import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import no.nav.klage.kodeverk.DokumentType
import no.nav.klage.kodeverk.Fagsystem
import no.nav.klage.kodeverk.PartIdType
import no.nav.klage.kodeverk.Tema
import java.time.LocalDateTime
import java.util.*

fun ikkeDistribuertDokumentEnhetMedVedleggOgToBrevMottakere() = DokumentEnhet(
    eier = SaksbehandlerIdent(navIdent = "A10101"),
    journalfoeringData = JournalfoeringData(
        sakenGjelder = PartId(
            type = PartIdType.PERSON,
            value = "20022012345"
        ),
        tema = Tema.OMS,
        sakFagsakId = "sakFagsakId",
        sakFagsystem = Fagsystem.FS36,
        kildeReferanse = "kildeReferanse",
        enhet = "Enhet",
        behandlingstema = "behandlingstema",
        tittel = "Tittel",
        brevKode = "brevKode",
        tilleggsopplysning = Tilleggsopplysning("key", "value")
    ),
    brevMottakere = listOf(
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
            rolle = Rolle.KOPIADRESSAT
        ),
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
            rolle = Rolle.HOVEDADRESSAT
        )
    ),
    dokumenter = listOf(
        OpplastetDokument(
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf",
            type = OpplastetDokument.OpplastetDokumentType.HOVEDDOKUMENT
        ),

        OpplastetDokument(
            mellomlagerId = "456",
            opplastet = LocalDateTime.now(),
            size = 1001L,
            name = "fil2.pdf",
            type = OpplastetDokument.OpplastetDokumentType.VEDLEGG
        )
    ),
    brevMottakerDistribusjoner = mutableListOf(),
    avsluttet = null,
    dokumentType = DokumentType.VEDTAK,
)

fun ikkeDistribuertDokumentEnhetUtenVedleggMedToBrevMottakere() = DokumentEnhet(
    eier = SaksbehandlerIdent(navIdent = "A10101"),
    journalfoeringData = JournalfoeringData(
        sakenGjelder = PartId(
            type = PartIdType.PERSON,
            value = "20022012345"
        ),
        tema = Tema.OMS,
        sakFagsakId = "sakFagsakId",
        sakFagsystem = Fagsystem.FS36,
        kildeReferanse = "kildeReferanse",
        enhet = "Enhet",
        behandlingstema = "behandlingstema",
        tittel = "Tittel",
        brevKode = "brevKode",
        tilleggsopplysning = Tilleggsopplysning("key", "value")
    ),
    brevMottakere = listOf(
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
            rolle = Rolle.KOPIADRESSAT
        ),
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
            rolle = Rolle.HOVEDADRESSAT
        )
    ),
    dokumenter = listOf(
        OpplastetDokument(
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf",
            type = OpplastetDokument.OpplastetDokumentType.HOVEDDOKUMENT
        ),
    ),
    brevMottakerDistribusjoner = mutableListOf(),
    avsluttet = null,
    dokumentType = DokumentType.VEDTAK,
)

fun journalfoertMenIkkeDistribuertDokumentEnhetMedEnBrevMottakere(
    brevMottakerId: UUID, dokumentId: UUID
): DokumentEnhet {
    val dokumentEnhet =
        DokumentEnhet(
            eier = SaksbehandlerIdent(navIdent = "A10101"),
            journalfoeringData = JournalfoeringData(
                sakenGjelder = PartId(
                    type = PartIdType.PERSON,
                    value = "20022012345"
                ),
                tema = Tema.OMS,
                sakFagsakId = "sakFagsakId",
                sakFagsystem = Fagsystem.FS36,
                kildeReferanse = "kildeReferanse",
                enhet = "Enhet",
                behandlingstema = "behandlingstema",
                tittel = "Tittel",
                brevKode = "brevKode",
                tilleggsopplysning = Tilleggsopplysning("key", "value")
            ),
            brevMottakere = listOf(
                BrevMottaker(
                    id = brevMottakerId,
                    partId = PartId(
                        type = PartIdType.PERSON,
                        value = "01011012345"
                    ),
                    navn = "Test Person",
                    rolle = Rolle.HOVEDADRESSAT
                )
            ),
            dokumenter = listOf(
                OpplastetDokument(
                    mellomlagerId = "123",
                    opplastet = LocalDateTime.now(),
                    size = 1000L,
                    name = "fil.pdf",
                    type = OpplastetDokument.OpplastetDokumentType.HOVEDDOKUMENT
                ),

                OpplastetDokument(
                    mellomlagerId = "456",
                    opplastet = LocalDateTime.now(),
                    size = 1001L,
                    name = "fil2.pdf",
                    type = OpplastetDokument.OpplastetDokumentType.VEDLEGG
                )
            ),
            brevMottakerDistribusjoner = mutableListOf(),
            avsluttet = null,
            dokumentType = DokumentType.VEDTAK,
        )

    dokumentEnhet.brevMottakerDistribusjoner = mutableListOf(
        BrevMottakerDistribusjon(
            brevMottakerId = brevMottakerId,
            opplastetDokumentId = dokumentId,
            journalpostId = JournalpostId("Whatever"),
            ferdigstiltIJoark = LocalDateTime.now(),
            dokdistReferanse = null,
            dokumentEnhet = dokumentEnhet,
        )
    )

    return dokumentEnhet
}

fun delvisDistribuertDokumentEnhetMedToBrevMottakere(
    brevMottakerId: UUID, dokumentId: UUID
): DokumentEnhet {
    val dokumentEnhet = DokumentEnhet(
        eier = SaksbehandlerIdent(navIdent = "A10101"),
        journalfoeringData = JournalfoeringData(
            sakenGjelder = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            tema = Tema.OMS,
            sakFagsakId = "sakFagsakId",
            sakFagsystem = Fagsystem.FS36,
            kildeReferanse = "kildeReferanse",
            enhet = "Enhet",
            behandlingstema = "behandlingstema",
            tittel = "Tittel",
            brevKode = "brevKode",
            tilleggsopplysning = Tilleggsopplysning("key", "value")
        ),
        brevMottakere = listOf(
            BrevMottaker(
                id = brevMottakerId,
                partId = PartId(
                    type = PartIdType.PERSON,
                    value = "01011012345"
                ),
                navn = "Test Person",
                rolle = Rolle.KOPIADRESSAT
            ),
            BrevMottaker(
                partId = PartId(
                    type = PartIdType.PERSON,
                    value = "20022012345"
                ),
                navn = "Mottaker Person",
                rolle = Rolle.HOVEDADRESSAT
            )
        ),
        dokumenter = listOf(
            OpplastetDokument(
                mellomlagerId = "123",
                opplastet = LocalDateTime.now(),
                size = 1000L,
                name = "fil.pdf",
                type = OpplastetDokument.OpplastetDokumentType.HOVEDDOKUMENT
            ),

            OpplastetDokument(
                mellomlagerId = "456",
                opplastet = LocalDateTime.now(),
                size = 1001L,
                name = "fil2.pdf",
                type = OpplastetDokument.OpplastetDokumentType.VEDLEGG
            )
        ),
        brevMottakerDistribusjoner = mutableListOf(),
        avsluttet = null,
        dokumentType = DokumentType.VEDTAK,
    )

    dokumentEnhet.brevMottakerDistribusjoner = mutableListOf(
        BrevMottakerDistribusjon(
            brevMottakerId = brevMottakerId,
            opplastetDokumentId = dokumentId,
            journalpostId = JournalpostId("Whatever"),
            ferdigstiltIJoark = LocalDateTime.now(),
            dokdistReferanse = null,
            dokumentEnhet = dokumentEnhet,
        )
    )

    return dokumentEnhet
}