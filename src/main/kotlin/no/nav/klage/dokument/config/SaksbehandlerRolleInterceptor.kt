package no.nav.klage.dokument.config

import no.nav.klage.dokument.exceptions.NoSaksbehandlerRoleException
import no.nav.klage.dokument.service.saksbehandler.InnloggetSaksbehandlerService
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.servlet.AsyncHandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SaksbehandlerRolleInterceptor(
    private val innloggetSaksbehandlerService: InnloggetSaksbehandlerService
) : AsyncHandlerInterceptor {

    @Throws(Exception::class)
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any?
    ): Boolean {
        val isSaksbehandler = innloggetSaksbehandlerService.erSaksbehandler()
        if (isSaksbehandler) {
            return true
        }
        throw NoSaksbehandlerRoleException("Bruker har ikke saksbehandlerrolle")
    }

}

@Configuration
class SaksbehandlerRolleInterceptorConfig(
    private val saksbehandlerRolleInterceptor: SaksbehandlerRolleInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(saksbehandlerRolleInterceptor).addPathPatterns("/ansatte/**")
    }
}
