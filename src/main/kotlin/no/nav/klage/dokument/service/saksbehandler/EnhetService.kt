package no.nav.klage.dokument.service.saksbehandler

import no.nav.klage.dokument.gateway.AxsysGateway
import org.springframework.stereotype.Service

@Service
class EnhetService(
    private val saksbehandlerService: SaksbehandlerService,
    private val axsysGateway: AxsysGateway
) {

    fun getAnsatteIEnhet(enhetId: String): List<String> {
        return axsysGateway.getSaksbehandlereIEnhet(enhetId).map { it.navIdent }
    }

    fun getLedereIEnhet(enhetId: String): List<String> {
        return axsysGateway.getSaksbehandlereIEnhet(enhetId)
            .filter { saksbehandlerService.erLeder(it.navIdent) }
            .map { it.navIdent }
    }

    fun getFagansvarligeIEnhet(enhetId: String): List<String> {
        return axsysGateway.getSaksbehandlereIEnhet(enhetId)
            .filter { saksbehandlerService.erFagansvarlig(it.navIdent) }
            .map { it.navIdent }
    }


}