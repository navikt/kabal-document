package no.nav.klage.dokument.service.distribusjon

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.klage.dokument.domain.dokument.DokumentEnhet
import no.nav.klage.dokument.service.DokumentEnhetService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class KlagebehandlingSchedulerService(
    private val klagebehandlingDistribusjonService: KlagebehandlingDistribusjonService,
    private val dokumentEnhetService: DokumentEnhetService
) {

    @Scheduled(fixedDelay = 240000, initialDelay = 240000)
    @SchedulerLock(name = "distribuerVedtak")
    fun distribuerVedtak() {

        val dokumentEnheter: List<DokumentEnhet> = dokumentEnhetService.findDokumentEnheterForDistribusjon()
        dokumentEnheter.forEach {
            klagebehandlingDistribusjonService.distribuerKlagebehandling(it)
        }
    }
}