package no.nav.klage.dokument.domain.kodeverk

//TODO: Jeg tror ikke disse er relevante. Kan de fjernes helt, eller gjøres om til f.eks hoved_mottaker og cc_mottaker?
enum class Rolle {
    SOEKER,
    SAKEN_GJELDER,
    PROSESSFULLMEKTIG,
    RELEVANT_TREDJEPART;
}