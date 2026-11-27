package no.nav.klage.dokument.clients.klagelookup

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.klage.dokument.util.TokenUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.mock.http.client.reactive.MockClientHttpRequest
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.Optional
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference

class KlageLookupClientTest {
    private val tokenUtil = mockk<TokenUtil>()

    @Test
    fun `getPerson posts fnr with app token and parses known name while ignoring other fields`() {
        val capturedRequest = AtomicReference<ClientRequest?>()
        val client =
            createClient(
                status = HttpStatusCode.valueOf(200),
                responseBody =
                    """{"sammensattNavn":"Syntetisk Navn","fnr":"01010100000","navn":{"fornavn":"Syntetisk"}}""",
                capturedRequest = capturedRequest,
            )

        val person = client.getPerson(fnr = SYNTHETIC_FNR)

        assertThat(person).isEqualTo(PersonResponse(sammensattNavn = "Syntetisk Navn"))
        val request = requireNotNull(capturedRequest.get())
        assertThat(request.method()).isEqualTo(HttpMethod.POST)
        assertThat(request.url().toString()).isEqualTo("$BASE_URL/person")
        assertThat(request.headers().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer $ACCESS_TOKEN")
        assertThat(requestBody(request)).isEqualTo("""{"fnr":"$SYNTHETIC_FNR"}""")
        verify(exactly = 1) { tokenUtil.getAppAccessTokenWithKlageLookupScope() }
    }

    @ParameterizedTest
    @ValueSource(ints = [404, 500, 503])
    fun `http errors do not expose response content`(status: Int) {
        assertFailureDoesNotLeak(
            status = HttpStatusCode.valueOf(status),
            responseBody = """{"error":"SYNTHETIC_RESPONSE_MARKER"}""",
        )
    }

    @Test
    fun `timeout stops lookup without exposing upstream exception content`() {
        every { tokenUtil.getAppAccessTokenWithKlageLookupScope() } returns ACCESS_TOKEN
        val webClient =
            WebClient
                .builder()
                .baseUrl(BASE_URL)
                .exchangeFunction { Mono.error(TimeoutException("SYNTHETIC_RESPONSE_MARKER")) }
                .build()
        val client = KlageLookupClient(klageLookupWebClient = webClient, tokenUtil = tokenUtil)

        val exception = assertThrows<RuntimeException> { client.getPerson(fnr = SYNTHETIC_FNR) }

        assertThat(exception.message).isEqualTo("Could not get person from klage-lookup.")
        assertThat(exception.cause).isNull()
    }

    @Test
    fun `empty response does not expose response content`() {
        assertFailureDoesNotLeak(
            status = HttpStatusCode.valueOf(200),
            responseBody = "",
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "not-json SYNTHETIC_RESPONSE_MARKER",
            """{"extra":"SYNTHETIC_RESPONSE_MARKER"}""",
            """{"sammensattNavn":null,"extra":"SYNTHETIC_RESPONSE_MARKER"}""",
        ],
    )
    fun `malformed or missing name response does not expose response content`(responseBody: String) {
        assertFailureDoesNotLeak(
            status = HttpStatusCode.valueOf(200),
            responseBody = responseBody,
        )
    }

    private fun assertFailureDoesNotLeak(
        status: HttpStatusCode,
        responseBody: String,
    ) {
        val exception =
            assertThrows<RuntimeException> {
                createClient(status = status, responseBody = responseBody).getPerson(fnr = SYNTHETIC_FNR)
            }

        assertThat(exception.message).isEqualTo("Could not get person from klage-lookup.")
        assertThat(exception.cause).isNull()
        assertThat(exception.toString()).doesNotContain("SYNTHETIC_RESPONSE_MARKER")
    }

    private fun createClient(
        status: HttpStatusCode,
        responseBody: String,
        capturedRequest: AtomicReference<ClientRequest?>? = null,
    ): KlageLookupClient {
        every { tokenUtil.getAppAccessTokenWithKlageLookupScope() } returns ACCESS_TOKEN
        val exchangeFunction =
            ExchangeFunction { request ->
                capturedRequest?.set(request)
                Mono.just(
                    ClientResponse
                        .create(status)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(responseBody)
                        .build(),
                )
            }
        val webClient =
            WebClient
                .builder()
                .baseUrl(BASE_URL)
                .exchangeFunction(exchangeFunction)
                .build()

        return KlageLookupClient(
            klageLookupWebClient = webClient,
            tokenUtil = tokenUtil,
        )
    }

    private fun requestBody(request: ClientRequest): String {
        val mockRequest = MockClientHttpRequest(request.method(), request.url())
        val bodyInserterContext =
            object : BodyInserter.Context {
                override fun messageWriters(): List<HttpMessageWriter<*>> = ExchangeStrategies.withDefaults().messageWriters()

                override fun serverRequest(): Optional<ServerHttpRequest> = Optional.empty()

                override fun hints(): Map<String, Any> = emptyMap()
            }

        request.body().insert(mockRequest as ClientHttpRequest, bodyInserterContext).block()
        return mockRequest.bodyAsString.block().orEmpty()
    }

    companion object {
        private const val BASE_URL = "https://klage-lookup.test"
        private const val ACCESS_TOKEN = "synthetic-access-token"
        private const val SYNTHETIC_FNR = "01010100000"
    }
}
