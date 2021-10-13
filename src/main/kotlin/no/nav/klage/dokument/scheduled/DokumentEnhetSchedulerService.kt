package no.nav.klage.dokument.scheduled

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.klage.dokument.repositories.DokumentEnhetRepository
import no.nav.klage.dokument.service.distribusjon.DokumentEnhetDistribusjonService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentEnhetSchedulerService(
    private val dokumentEnhetDistribusjonService: DokumentEnhetDistribusjonService,
    private val dokumentEnhetRepository: DokumentEnhetRepository
) {
    @Scheduled(fixedDelay = 240000, initialDelay = 240000)
    @SchedulerLock(name = "distribuerVedtak")
    fun distribuerVedtak() {

        val dokumentEnhetIder: List<UUID> = dokumentEnhetRepository.findDokumentEnheterForDistribusjon()
        dokumentEnhetIder.forEach {
            val dokumentEnhet = dokumentEnhetRepository.findById(it)!!
            val distribuertDokumentEnhet = dokumentEnhetDistribusjonService.distribuerDokumentEnhet(dokumentEnhet)
            dokumentEnhetRepository.saveOrUpdate(distribuertDokumentEnhet)
        }
    }
}