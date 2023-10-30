package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.view.*
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
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
            journalpostIdList = journalpostIdList,
            dokumentUnderArbeidWithJoarkReferencesList = getDokumentUnderArbeidWithJoarkReferencesList(dokumentEnhet)
        )
    }

    private fun getDokumentUnderArbeidWithJoarkReferencesList(dokumentEnhet: DokumentEnhet): List<DokumentUnderArbeidWithJoarkReferences> {
        val output = mutableListOf<DokumentUnderArbeidWithJoarkReferences>()

        dokumentEnhet.hovedDokument?.let { getDokumentUnderArbeidWithJoarkReferences(it) }?.let { output.add(it) }

        dokumentEnhet.vedlegg.map {
            output.add(getDokumentUnderArbeidWithJoarkReferences(it))
        }

        return output
    }

    private fun getDokumentUnderArbeidWithJoarkReferences(opplastetDokument: OpplastetDokument): DokumentUnderArbeidWithJoarkReferences {
        return DokumentUnderArbeidWithJoarkReferences(
            dokumentUnderArbeidReferanse = opplastetDokument.dokumentUnderArbeidReferanse,
            joarkReferenceList = opplastetDokument.dokumentInfoReferenceList.map { getJoarkReference(it) },
        )
    }

    private fun getJoarkReference(it: no.nav.klage.dokument.domain.dokument.DokumentInfoReference): JoarkReference {
        return JoarkReference(
            journalpostId = it.journalpostId,
            dokumentInfoId = it.dokumentInfoId
        )
    }
}

