package no.nav.klage.dokument.scheduled

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.service.DokumentEnhetDistribusjonService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DokumentEnhetSchedulerService(
    private val dokumentEnhetDistribusjonService: DokumentEnhetDistribusjonService,
    private val dokumentEnhetRepository: DokumentEnhetRepository
) {

    @Scheduled(fixedDelay = 240000, initialDelay = 240000)
    @SchedulerLock(name = "distribuerVedtak")
    fun distribuerVedtak() {

        val dokumentEnheter: List<DokumentEnhet> = dokumentEnhetRepository.findDokumentEnheterForDistribusjon()
        dokumentEnheter.forEach {
            dokumentEnhetDistribusjonService.distribuerDokumentEnhet(it)
        }
    }
}