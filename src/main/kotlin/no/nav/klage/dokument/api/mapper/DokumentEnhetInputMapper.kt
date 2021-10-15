package no.nav.klage.dokument.api.mapper

import no.nav.klage.dokument.api.input.BrevMottakerInput
import no.nav.klage.dokument.api.input.JournalfoeringDataInput
import no.nav.klage.dokument.api.input.PartIdInput
import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData
import no.nav.klage.dokument.domain.dokument.PartId
import no.nav.klage.dokument.domain.kodeverk.Fagsystem
import no.nav.klage.dokument.domain.kodeverk.PartIdType
import no.nav.klage.dokument.domain.kodeverk.Rolle
import no.nav.klage.dokument.domain.kodeverk.Tema
import no.nav.klage.dokument.exceptions.DokumentEnhetNotValidException
import no.nav.klage.dokument.util.getLogger
import no.nav.klage.dokument.util.getSecureLogger
import org.springframework.stereotype.Service

@Service
class DokumentEnhetInputMapper {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun mapBrevMottakereInput(brevMottakere: List<BrevMottakerInput>): List<BrevMottaker> =
        brevMottakere.map { mapBrevMottakerInput(it) }

    fun mapBrevMottakerInput(brevMottakerInput: BrevMottakerInput): BrevMottaker =
        try {
            BrevMottaker(
                partId = mapPartIdInput(brevMottakerInput.partId),
                navn = brevMottakerInput.navn,
                rolle = Rolle.valueOf(brevMottakerInput.rolle)

            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
        }

    fun mapJournalfoeringDataInput(input: JournalfoeringDataInput): JournalfoeringData =
        try {
            JournalfoeringData(
                sakenGjelder = mapPartIdInput(input.sakenGjelder),
                tema = Tema.valueOf(input.tema),
                sakFagsakId = input.sakFagsakId,
                sakFagsystem = input.sakFagsystem?.let { Fagsystem.valueOf(it) },
                kildeReferanse = input.kildeReferanse,
                enhet = input.enhet //TODO: Validering??
            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
        }

    private fun mapPartIdInput(partIdInput: PartIdInput) =
        try {
            PartId(
                type = PartIdType.valueOf(partIdInput.type),
                value = partIdInput.value
            )
        } catch (iae: IllegalArgumentException) {
            logger.warn("Data fra klient er ikke gyldig", iae)
            throw DokumentEnhetNotValidException("Ulovlig input: ${iae.message}")
        }


}