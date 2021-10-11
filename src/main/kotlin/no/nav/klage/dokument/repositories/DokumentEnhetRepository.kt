package no.nav.klage.dokument.repositories

import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
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