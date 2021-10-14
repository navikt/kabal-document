package no.nav.klage.dokument.exceptions

class AttachmentTooLargeException(msg: String = "TOO_LARGE") : ValidationException(msg) {
    @Synchronized
    fun fillInStackTrace(): Throwable? {
        //Remove stacktrace
        return this
    }
}

class AttachmentEncryptedException(msg: String = "ENCRYPTED") : ValidationException(msg)
class AttachmentIsEmptyException(msg: String = "EMPTY") : ValidationException(msg)
class AttachmentHasVirusException(msg: String = "VIRUS") : ValidationException(msg)
