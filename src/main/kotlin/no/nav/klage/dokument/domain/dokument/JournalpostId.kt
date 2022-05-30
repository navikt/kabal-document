package no.nav.klage.dokument.domain.dokument

data class JournalpostId(val value: String)

data class JournalpostIdOgDokumentInfo(val journalpostId: JournalpostId, val dokumentInfoIdList: List<String>)