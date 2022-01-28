package no.nav.klage.dokument.service

import no.nav.klage.dokument.domain.dokument2.HovedDokument
import no.nav.klage.dokument.repositories.HovedDokumentRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class DokumentService(
    private val hovedDokumentRepository: HovedDokumentRepository
) {

    fun finnOgFerdigstillHovedDokument(hovedDokumentId: UUID): HovedDokument {

        val hovedDokument = hovedDokumentRepository.getById(hovedDokumentId)
        hovedDokument.ferdigstillHvisIkkeAlleredeFerdigstilt()
        return hovedDokument
    }
}