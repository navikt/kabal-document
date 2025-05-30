package no.nav.klage.dokument.clients.saf.graphql

import kotlin.io.readText
import kotlin.jvm.java
import kotlin.text.replace

data class HentJournalpostGraphqlQuery(
    val query: String,
    val variables: JournalpostVariables
)

data class JournalpostVariables(val journalpostId: String)

fun hentJournalpostQuery(
    journalpostId: String
): HentJournalpostGraphqlQuery {
    val journalpostProperties = HentJournalpostGraphqlQuery::class.java.getResource("/saf/journalpostProperties.txt")
        .readText()
    val query =
        HentJournalpostGraphqlQuery::class.java.getResource("/saf/hentJournalpost.graphql")
            .readText()
            .replace("<replace>", journalpostProperties)
            .replace("[\n\r]", "")
    return HentJournalpostGraphqlQuery(query, JournalpostVariables(journalpostId))
}