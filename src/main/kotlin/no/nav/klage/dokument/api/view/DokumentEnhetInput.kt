package no.nav.klage.dokument.api.view

data class DokumentEnhetInput(
    val brevMottakere: List<BrevMottakerInput>,
    //TODO Send inn først ved ferdigstilling?
    val journalfoeringData: JournalfoeringDataInput
)
