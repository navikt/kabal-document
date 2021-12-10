package no.nav.klage.dokument.service.saksbehandler

import no.nav.klage.dokument.domain.saksbehandler.EnheterMedLovligeTemaer
import no.nav.klage.dokument.gateway.AxsysGateway
import no.nav.klage.dokument.gateway.AzureGateway
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.kodeverk.Tema
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class SaksbehandlerService(
    private val azureGateway: AzureGateway,
    private val axsysGateway: AxsysGateway,
    @Value("\${ROLE_KLAGE_SAKSBEHANDLER}") private val saksbehandlerRole: String,
    @Value("\${ROLE_KLAGE_FAGANSVARLIG}") private val fagansvarligRole: String,
    @Value("\${ROLE_KLAGE_LEDER}") private val lederRole: String,
    @Value("\${ROLE_KLAGE_MERKANTIL}") private val merkantilRole: String,
    @Value("\${ROLE_KLAGE_FORTROLIG}") private val kanBehandleFortroligRole: String,
    @Value("\${ROLE_KLAGE_STRENGT_FORTROLIG}") private val kanBehandleStrengtFortroligRole: String,
    @Value("\${ROLE_KLAGE_EGEN_ANSATT}") private val kanBehandleEgenAnsattRole: String
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)

        val saksbehandlerNameCache = mutableMapOf<String, String>()

        const val MAX_AMOUNT_IDENTS_IN_GRAPH_QUERY = 15
    }

    fun harTilgangTilEnhetOgTema(ident: String, enhetId: String, tema: Tema): Boolean {
        return getEnheterMedTemaerForSaksbehandler(ident).enheter.firstOrNull { it.enhetId == enhetId }?.temaer?.contains(
            tema
        ) ?: false
    }

    fun harTilgangTilEnhet(ident: String, enhetId: String): Boolean {
        return getEnheterMedTemaerForSaksbehandler(ident).enheter.firstOrNull { it.enhetId == enhetId } != null
    }

    fun harTilgangTilTema(ident: String, tema: Tema): Boolean {
        return getEnheterMedTemaerForSaksbehandler(ident).enheter.flatMap { it.temaer }.contains(tema)
    }

    fun getEnheterMedTemaerForSaksbehandler(ident: String): EnheterMedLovligeTemaer =
        axsysGateway.getEnheterMedTemaerForSaksbehandler(ident)

    fun getAlleSaksbehandlerIdenter(): List<String> {
        return azureGateway.getGroupMembersNavIdents(saksbehandlerRole)
    }

    fun getNamesForSaksbehandlere(identer: Set<String>): Map<String, String> {
        logger.debug("Fetching names for saksbehandlere from Microsoft Graph")

        val identerNotInCache = identer.toMutableSet()
        identerNotInCache -= saksbehandlerNameCache.keys
        logger.debug("Only fetching identer not in cache: {}", identerNotInCache)

        val chunkedList = identerNotInCache.chunked(MAX_AMOUNT_IDENTS_IN_GRAPH_QUERY)

        val measuredTimeMillis = measureTimeMillis {
            saksbehandlerNameCache += azureGateway.getAllDisplayNames(chunkedList)
        }
        logger.debug("It took {} millis to fetch all names", measuredTimeMillis)

        return saksbehandlerNameCache
    }

    fun erFagansvarlig(ident: String): Boolean = getRoller(ident).hasRole(fagansvarligRole)

    fun erLeder(ident: String): Boolean = getRoller(ident).hasRole(lederRole)

    fun erSaksbehandler(ident: String): Boolean = getRoller(ident).hasRole(saksbehandlerRole)

    fun kanBehandleFortrolig(ident: String): Boolean = getRoller(ident).hasRole(kanBehandleFortroligRole)

    fun kanBehandleStrengtFortrolig(ident: String): Boolean =
        getRoller(ident).hasRole(kanBehandleStrengtFortroligRole)

    fun kanBehandleEgenAnsatt(ident: String): Boolean = getRoller(ident).hasRole(kanBehandleEgenAnsattRole)

    private fun getRoller(ident: String): List<String> = azureGateway.getRolleIder(ident)

    private fun List<String>.hasRole(role: String) = any { it.contains(role) }
}