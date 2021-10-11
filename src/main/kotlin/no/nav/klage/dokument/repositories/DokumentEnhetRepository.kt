package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentEnhetRepository {

    fun findDokumentEnheterForDistribusjon(): List<DokumentEnhet> {
        return emptyList()
    }

    fun findById(dokumentEnhetId: UUID): DokumentEnhet? {
        return null
    }

    fun save(dokumentEnhet: DokumentEnhet): DokumentEnhet {
        return dokumentEnhet
    }
}