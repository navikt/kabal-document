package no.nav.klage.dokument.exceptions


open class ValidationException(msg: String) : RuntimeException(msg)

class DokumentEnhetNotFoundException(msg: String) : ValidationException(msg)

class JournalpostNotFoundException(msg: String) : ValidationException(msg)

class DokumentEnhetNotValidException(msg: String) : ValidationException(msg)
