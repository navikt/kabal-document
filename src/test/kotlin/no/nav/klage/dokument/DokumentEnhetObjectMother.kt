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

fun ferdigDistribuertDokumentEnhet(): DokumentEnhet {
    val dokumentEnhetId = UUID.randomUUID()
    return DokumentEnhet(
        id = dokumentEnhetId,
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
        hovedDokument = OpplastetDokument(
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf"
        ),
        vedlegg = listOf(
            OpplastetDokument(
                mellomlagerId = "456",
                opplastet = LocalDateTime.now(),
                size = 1001L,
                name = "fil2.pdf"
            )
        ),
        brevMottakerDistribusjoner = listOf(
            BrevMottakerDistribusjon(
                brevMottakerId = UUID.randomUUID(),
                opplastetDokumentId = UUID.randomUUID(),
                journalpostId = JournalpostId("Whatever"),
                ferdigstiltIJoark = LocalDateTime.now(),
                dokdistReferanse = UUID.randomUUID(),
                dokumentEnhetId = dokumentEnhetId,
            )
        ),
        avsluttet = LocalDateTime.now(),
        dokumentType = DokumentType.VEDTAK,
    )
}

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
    hovedDokument = OpplastetDokument(
        mellomlagerId = "123",
        opplastet = LocalDateTime.now(),
        size = 1000L,
        name = "fil.pdf"
    ),
    vedlegg = listOf(
        OpplastetDokument(
            mellomlagerId = "456",
            opplastet = LocalDateTime.now(),
            size = 1001L,
            name = "fil2.pdf"
        )
    ),
    brevMottakerDistribusjoner = listOf(),
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
    hovedDokument = OpplastetDokument(
        mellomlagerId = "123",
        opplastet = LocalDateTime.now(),
        size = 1000L,
        name = "fil.pdf"
    ),
    brevMottakerDistribusjoner = listOf(),
    avsluttet = null,
    dokumentType = DokumentType.VEDTAK,
)

fun journalfoertMenIkkeDistribuertDokumentEnhetMedEnBrevMottakere(
    brevMottakerId: UUID, dokumentId: UUID
): DokumentEnhet {
    val dokumentEnhetId = UUID.randomUUID()
    return DokumentEnhet(
        id = dokumentEnhetId,
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
        hovedDokument = OpplastetDokument(
            id = dokumentId,
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf"
        ),
        vedlegg = listOf(
            OpplastetDokument(
                mellomlagerId = "456",
                opplastet = LocalDateTime.now(),
                size = 1001L,
                name = "fil2.pdf"
            )
        ),
        brevMottakerDistribusjoner = listOf(
            BrevMottakerDistribusjon(
                brevMottakerId = brevMottakerId,
                opplastetDokumentId = dokumentId,
                journalpostId = JournalpostId("Whatever"),
                ferdigstiltIJoark = LocalDateTime.now(),
                dokdistReferanse = null,
                dokumentEnhetId = dokumentEnhetId,
            )
        ),
        avsluttet = null,
        dokumentType = DokumentType.VEDTAK,
    )
}

fun delvisDistribuertDokumentEnhetMedToBrevMottakere(
    brevMottakerId: UUID, dokumentId: UUID
): DokumentEnhet {
    val dokumentEnhetId = UUID.randomUUID()
    return DokumentEnhet(
        id = dokumentEnhetId,
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
        hovedDokument = OpplastetDokument(
            id = dokumentId,
            mellomlagerId = "123",
            opplastet = LocalDateTime.now(),
            size = 1000L,
            name = "fil.pdf"
        ),
        vedlegg = listOf(
            OpplastetDokument(
                mellomlagerId = "456",
                opplastet = LocalDateTime.now(),
                size = 1001L,
                name = "fil2.pdf"
            )
        ),
        brevMottakerDistribusjoner = listOf(
            BrevMottakerDistribusjon(
                brevMottakerId = brevMottakerId,
                opplastetDokumentId = dokumentId,
                journalpostId = JournalpostId("Whatever"),
                ferdigstiltIJoark = LocalDateTime.now(),
                dokdistReferanse = UUID.randomUUID(),
                dokumentEnhetId = dokumentEnhetId,
            )
        ),
        avsluttet = null,
        dokumentType = DokumentType.VEDTAK,
    )
}

fun dokumentEnhetUtenBrevMottakereOgHovedDokument() = DokumentEnhet(
    eier = SaksbehandlerIdent(navIdent = "A10101"),
    journalfoeringData = JournalfoeringData(
        sakenGjelder = PartId(
            type = PartIdType.PERSON,
            value = "20022012345"
        ),
        tema = Tema.OMS,
        sakFagsakId = null,
        sakFagsystem = null,
        kildeReferanse = "kildeReferanse",
        enhet = "Enhet",
        behandlingstema = "behandlingstema",
        tittel = "Tittel",
        brevKode = "brevKode",
        tilleggsopplysning = null
    ),
    brevMottakere = emptyList(),
    hovedDokument = null,
    vedlegg = emptyList(),
    brevMottakerDistribusjoner = emptyList(),
    avsluttet = null,
    dokumentType = DokumentType.VEDTAK,
)

fun dokumentEnhetForIntegrasjonstest() = DokumentEnhet(
    eier = SaksbehandlerIdent(navIdent = "A10101"),
    journalfoeringData = JournalfoeringData(
        sakenGjelder = PartId(
            type = PartIdType.PERSON,
            value = "20022012345"
        ),
        tema = Tema.OMS,
        sakFagsakId = null,
        sakFagsystem = null,
        kildeReferanse = "kildeReferanse",
        enhet = "Enhet",
        behandlingstema = "behandlingstema",
        tittel = "Tittel",
        brevKode = "brevKode",
        tilleggsopplysning = null
    ),
    brevMottakere = emptyList(),
    hovedDokument = null,
    vedlegg = emptyList(),
    brevMottakerDistribusjoner = emptyList(),
    avsluttet = null,
    dokumentType = DokumentType.VEDTAK,
)