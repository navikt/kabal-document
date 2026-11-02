package no.nav.klage.dokument.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterProperties
import org.springframework.boot.servlet.filter.OrderedRequestContextFilter
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.annotation.Order

/**
 * MockMvc neither reproduces the servlet filter order (Spring Security is appended last) nor the
 * missing request scope (the test framework binds a request to the thread), so check the order directly.
 */
class SaksbehandlerFilterTest {
    private val order = AnnotationUtils.findAnnotation(SaksbehandlerFilter::class.java, Order::class.java)!!.value

    @Test
    fun `runs after RequestContextFilter, which activates the request scope`() {
        assertThat(order).isGreaterThan(OrderedRequestContextFilter().order)
    }

    @Test
    fun `runs after Spring Security, so unauthenticated requests never reach the database lookup`() {
        assertThat(order).isGreaterThan(SecurityFilterProperties.DEFAULT_FILTER_ORDER)
    }
}
