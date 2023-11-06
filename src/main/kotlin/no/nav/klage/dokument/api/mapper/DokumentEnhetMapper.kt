package no.nav.klage.dokument.api.mapper


import no.nav.klage.dokument.api.view.*
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.domain.dokument.OpplastetDokument
import no.nav.klage.dokument.util.getLogger
import org.springframework.stereotype.Service

@Service
class DokumentEnhetMapper {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun mapToDokumentEnhetView(dokumentEnhet: DokumentEnhet): DokumentEnhetView {
        return DokumentEnhetView(
            id = dokumentEnhet.id.toString(),
        )
    }

    fun mapToDokumentEnhetFullfoertView(dokumentEnhet: DokumentEnhet): DokumentEnhetFullfoertView {
        val journalpostIdList = dokumentEnhet.brevMottakerDistribusjoner.map {
            it.journalpostId!!
        }
        val dokumentEnhetFullfoertView = DokumentEnhetFullfoertView(
            journalpostIdList.map {
                BrevMottakerWithJoarkAndDokDistInfo(
                    journalpostId = JournalpostId(value = it),
                )
            },
            journalpostIdList = journalpostIdList,
            sourceReferenceWithJoarkReferencesList = getSourceReferenceWithJoarkReferencesList(dokumentEnhet)
        )

        return dokumentEnhetFullfoertView
    }

    private fun getSourceReferenceWithJoarkReferencesList(dokumentEnhet: DokumentEnhet): List<SourceReferenceWithJoarkReferences> {
        val output = mutableListOf<SourceReferenceWithJoarkReferences>()

        dokumentEnhet.hovedDokument?.let { getSourceReferenceWithJoarkReferences(it) }?.let { output.add(it) }

        dokumentEnhet.vedlegg.map {
            output.add(getSourceReferenceWithJoarkReferences(it))
        }

        return output
    }

    private fun getSourceReferenceWithJoarkReferences(opplastetDokument: OpplastetDokument): SourceReferenceWithJoarkReferences {
        return SourceReferenceWithJoarkReferences(
            sourceReference = opplastetDokument.sourceReference,
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

