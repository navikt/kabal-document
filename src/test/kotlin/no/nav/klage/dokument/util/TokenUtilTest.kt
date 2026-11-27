package no.nav.klage.dokument.util

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.klage.auth.TexasClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TokenUtilTest {
    @Test
    fun `klage lookup app token uses klage lookup scope`() {
        val texasClient = mockk<TexasClient>()
        val klageLookupScope = "api://cluster.namespace.klage-lookup/.default"
        every { texasClient.machineToMachineToken(klageLookupScope) } returns "synthetic-access-token"
        val tokenUtil =
            TokenUtil(
                texasClient = texasClient,
                kabalFileApiScope = "api://cluster.namespace.kabal-file-api/.default",
                safScope = "api://saf/.default",
                klageLookupScope = klageLookupScope,
                dokarkivScope = "api://dokarkiv/.default",
                dokdistScope = "api://dokdist/.default",
            )

        assertThat(tokenUtil.getAppAccessTokenWithKlageLookupScope()).isEqualTo("synthetic-access-token")

        verify(exactly = 1) { texasClient.machineToMachineToken(klageLookupScope) }
        confirmVerified(texasClient)
    }
}
