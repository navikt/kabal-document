package no.nav.klage.dokument.api.view

import no.nav.klage.dokument.domain.dokument.BrevMottaker
import no.nav.klage.dokument.domain.dokument.JournalfoeringData

data class DokumentEnhetInput(
    val brevMottakere: List<BrevMottaker>,
    //TODO Send inn først ved ferdigstilling?
    val journalfoeringData: JournalfoeringData
) {

    //TODO: Rydd opp, lag input-klasse

}
