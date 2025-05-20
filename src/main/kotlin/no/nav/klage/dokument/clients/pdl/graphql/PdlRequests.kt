package no.nav.klage.dokument.clients.pdl.graphql

import java.net.URL

data class PersonGraphqlQuery(
    val query: String,
    val variables: IdentVariables
)

data class IdentVariables(
    val ident: String,
    val grupper: Array<IdentType>? = null,
)

fun hentPersonQuery(ident: String): PersonGraphqlQuery {
    val query =
        PersonGraphqlQuery::class.java.getResource("/pdl/hentPerson.graphql").cleanForGraphql()
    return PersonGraphqlQuery(query, IdentVariables(ident))
}

enum class IdentType {
    FOLKEREGISTERIDENT,
    AKTORID,
}

fun URL.cleanForGraphql() = readText().replace("[\n\r]", "")