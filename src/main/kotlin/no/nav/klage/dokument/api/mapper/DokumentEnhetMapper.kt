package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.view.BrevMottakerWithJoarkAndDokDistInfo
import no.nav.klage.dokument.api.view.DokumentEnhetFullfoertView
import no.nav.klage.dokument.api.view.DokumentEnhetView
import no.nav.klage.dokument.api.view.JournalpostId
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import org.springframework.stereotype.Service

@Service
class DokumentEnhetMapper {
    fun mapToDokumentEnhetView(dokumentEnhet: DokumentEnhet): DokumentEnhetView {
        return DokumentEnhetView(
            id = dokumentEnhet.id.toString(),
        )
    }

    fun mapToDokumentEnhetFullfoertView(dokumentEnhet: DokumentEnhet): DokumentEnhetFullfoertView {
        val journalpostIdList = dokumentEnhet.brevMottakerDistribusjoner.map {
            it.journalpostId!!
        }
        return DokumentEnhetFullfoertView(
            journalpostIdList.map {
                BrevMottakerWithJoarkAndDokDistInfo(
                    journalpostId = JournalpostId(value = it),
                )
            },
            journalpostIdList = journalpostIdList
        )
    }
}

