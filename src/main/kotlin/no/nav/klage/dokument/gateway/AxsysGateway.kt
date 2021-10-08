package no.nav.klage.dokument.gateway

import no.nav.klage.dokument.domain.saksbehandler.EnheterMedLovligeTemaer
import no.nav.klage.dokument.domain.saksbehandler.SaksbehandlerIdent

interface AxsysGateway {

    fun getEnheterMedTemaerForSaksbehandler(ident: String): EnheterMedLovligeTemaer
    fun getSaksbehandlereIEnhet(enhetId: String): List<SaksbehandlerIdent>
}