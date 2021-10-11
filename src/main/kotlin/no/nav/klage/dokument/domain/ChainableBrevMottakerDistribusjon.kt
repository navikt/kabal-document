package no.nav.klage.dokument.domain

import no.nav.klage.dokument.util.getLogger

class ChainableOperation<I>(val value: I, private val isSuccess: Boolean) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun chain(operation: (I) -> I): ChainableOperation<I> =
        if (isSuccess) {
            kotlin.runCatching {
                ChainableOperation(operation.invoke(this.value), true)
            }.onFailure {
                logger.warn("Operation failed", it)
            }.getOrDefault(ChainableOperation(value, false))
        } else {
            this
        }

}