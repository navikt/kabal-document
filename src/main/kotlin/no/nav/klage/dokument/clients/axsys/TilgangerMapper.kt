package no.nav.klage.dokument.clients.axsys

import no.nav.klage.dokument.domain.kodeverk.Tema
import no.nav.klage.dokument.domain.saksbehandler.EnhetMedLovligeTemaer
import no.nav.klage.dokument.domain.saksbehandler.EnheterMedLovligeTemaer
import no.nav.klage.dokument.util.getLogger
import org.springframework.stereotype.Component

@Component
class TilgangerMapper {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun mapTilgangerToEnheterMedLovligeTemaer(tilganger: Tilganger): EnheterMedLovligeTemaer {

        return EnheterMedLovligeTemaer(tilganger.enheter.map { enhet ->
            EnhetMedLovligeTemaer(
                enhet.enhetId,
                enhet.navn,
                enhet.temaer?.mapNotNull { mapTemaNavnToTema(it) } ?: emptyList())
        })
    }

    private fun mapTemaNavnToTema(tema: String): Tema? =
        try {
            Tema.valueOf(tema)
        } catch (e: Exception) {
            logger.warn("Unable to map Tema $tema. Ignoring and moving on", e)
            null
        }
}