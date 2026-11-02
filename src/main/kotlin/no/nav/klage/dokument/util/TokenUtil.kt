package no.nav.klage.dokument.util

import no.nav.klage.auth.TexasClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenUtil(
    private val texasClient: TexasClient,
    @Value($$"api://${NAIS_CLUSTER_NAME}.${NAIS_NAMESPACE}.${KABAL_FILE_API_APP_NAME}/.default")
    private val kabalFileApiScope: String,
    @Value($$"api://${SAF_SCOPE}/.default")
    private val safScope: String,
    @Value($$"api://${PDL_SCOPE}/.default")
    private val pdlScope: String,
    @Value($$"api://${DOKARKIV_SCOPE}/.default")
    private val dokarkivScope: String,
    @Value($$"api://${DOKDIST_SCOPE}/.default")
    private val dokdistScope: String,
) {
    fun getAppAccessTokenWithKabalFileApiScope(): String = texasClient.machineToMachineToken(kabalFileApiScope)

    fun getAppAccessTokenWithSafScope(): String = texasClient.machineToMachineToken(safScope)

    fun getAppAccessTokenWithPdlScope(): String = texasClient.machineToMachineToken(pdlScope)

    fun getSaksbehandlerAccessTokenWithDokarkivScope(): String = texasClient.onBehalfOfCurrentUser(dokarkivScope)

    fun getAppAccessTokenWithDokarkivScope(): String = texasClient.machineToMachineToken(dokarkivScope)

    fun getAppAccessTokenWithDokdistScope(): String = texasClient.machineToMachineToken(dokdistScope)
}
