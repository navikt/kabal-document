package no.nav.klage.dokument.api.input

data class DokumentEnhetInput(
    val brevMottakere: List<BrevMottakerInput>,
    val journalfoeringData: JournalfoeringDataInput,
    val dokumentType: String? = null,
    val eksternReferanse: String? = null,
)

data class DokumentEnhetInputV2(
    val brevMottakere: List<BrevMottakerInput>? = null,
    val journalfoeringData: JournalfoeringDataInput? = null,
    val dokumentType: String,
    val eksternReferanse: String,
)
