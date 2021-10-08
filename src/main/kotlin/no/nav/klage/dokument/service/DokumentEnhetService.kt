package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentEnhetService {

    fun findDokumentEnheterForDistribusjon(): List<DokumentEnhet> {
        return emptyList()
    }

    fun findById(dokumentEnhetId: UUID): DokumentEnhet? {
        return null
    }
}