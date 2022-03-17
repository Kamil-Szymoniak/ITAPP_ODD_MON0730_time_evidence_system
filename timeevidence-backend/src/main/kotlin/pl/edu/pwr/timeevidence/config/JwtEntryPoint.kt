package pl.edu.pwr.timeevidence.config

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class)
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, e: AuthenticationException) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.localizedMessage)
    }
}