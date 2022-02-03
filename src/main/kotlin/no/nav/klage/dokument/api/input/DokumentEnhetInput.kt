package no.nav.klage.dokument.api.input

data class DokumentEnhetInput(
    val brevMottakere: List<BrevMottakerInput>,
    val journalfoeringData: JournalfoeringDataInput
)

data class DokumentEnhetWithDokumentreferanserInput(
    val brevMottakere: List<BrevMottakerInput>,
    val journalfoeringData: JournalfoeringDataInput,
    val dokumentreferanser: DokumentInput,
)