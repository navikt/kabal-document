package no.nav.klage.dokument

import no.nav.klage.dokument.domain.dokument.*
import no.nav.klage.dokument.domain.kodeverk.Fagsystem
import no.nav.klage.dokument.domain.kodeverk.PartIdType
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.kodeverk.Tema
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent
import java.time.LocalDateTime
import java.util.*

fun ferdigDistribuertDokumentEnhet() = DokumentEnhet(
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
        enhet = "Enhet"
    ),
    brevMottakere = listOf(
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
            rolle = Rolle.SOEKER
        ),
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
            rolle = Rolle.PROSESSFULLMEKTIG
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
            dokdistReferanse = UUID.randomUUID()
        )
    ),
    avsluttet = LocalDateTime.now(),
)

fun ikkeDistribuertDokumentEnhetMedToBrevMottakere() = DokumentEnhet(
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
        enhet = "Enhet"
    ),
    brevMottakere = listOf(
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
            rolle = Rolle.SOEKER
        ),
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
            rolle = Rolle.PROSESSFULLMEKTIG
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
)

fun journalfoertMenIkkeDistribuertDokumentEnhetMedEnBrevMottakere(
    brevMottakerId: UUID, dokumentId: UUID
) = DokumentEnhet(
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
        enhet = "Enhet"
    ),
    brevMottakere = listOf(
        BrevMottaker(
            id = brevMottakerId,
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
            rolle = Rolle.SOEKER
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
            dokdistReferanse = null
        )
    ),
    avsluttet = null,
)

fun delvisDistribuertDokumentEnhetMedToBrevMottakere(
    brevMottakerId: UUID, dokumentId: UUID
) = DokumentEnhet(
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
        enhet = "Enhet"
    ),
    brevMottakere = listOf(
        BrevMottaker(
            id = brevMottakerId,
            partId = PartId(
                type = PartIdType.PERSON,
                value = "01011012345"
            ),
            navn = "Test Person",
            rolle = Rolle.SOEKER
        ),
        BrevMottaker(
            partId = PartId(
                type = PartIdType.PERSON,
                value = "20022012345"
            ),
            navn = "Mottaker Person",
            rolle = Rolle.PROSESSFULLMEKTIG
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
            dokdistReferanse = UUID.randomUUID()
        )
    ),
    avsluttet = null,
)

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
        enhet = "Enhet"
    ),
    brevMottakere = emptyList(),
    hovedDokument = null,
    vedlegg = emptyList(),
    brevMottakerDistribusjoner = emptyList(),
    avsluttet = null,
)