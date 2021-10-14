package no.nav.klage.dokument.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ChainableOperationTest {

    data class Operation(val numberOfSuccesses: Int = 0) {
        fun run(externalOperation: () -> (Unit)): Operation {
            externalOperation.invoke()
            return this.copy(numberOfSuccesses = numberOfSuccesses + 1)
        }
    }

    @Test
    fun `test run`() {
        val chainableOperation = ChainableOperation(Operation(), true)
        val numberOfSuccesses = chainableOperation
            .chain { operation -> operation.run {} }
            .chain { operation -> operation.run {} }
            .value.numberOfSuccesses
        assertThat(numberOfSuccesses).isEqualTo(2)
    }

    @Test
    fun `test run 2`() {
        val chainableOperation = ChainableOperation(Operation(), false)
        val numberOfSuccesses = chainableOperation
            .chain { operation -> operation.run {} }
            .chain { operation -> operation.run {} }
            .value.numberOfSuccesses
        assertThat(numberOfSuccesses).isEqualTo(0)
    }

    @Test
    fun `test run 3`() {
        val chainableOperation = ChainableOperation(Operation(), true)
        val numberOfSuccesses = chainableOperation
            .chain { operation -> operation.run {} }
            .chain { operation -> operation.run { throw RuntimeException("Smack!") } }
            .value.numberOfSuccesses
        assertThat(numberOfSuccesses).isEqualTo(1)
    }

    @Test
    fun `test run 4`() {
        val chainableOperation = ChainableOperation(Operation(), true)
        val numberOfSuccesses = chainableOperation
            .chain { operation -> operation.run { throw RuntimeException("Smack!") } }
            .chain { operation -> operation.run {} }
            .value.numberOfSuccesses
        assertThat(numberOfSuccesses).isEqualTo(0)
    }
}