package pl.edu.pwr.timeevidence.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUtils: JwtUtils
    @Autowired
    private lateinit var cookieService: CookieService
    @Autowired
    private lateinit var userDetailsServiceImplementation: UserDetailsServiceImplementation

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        try {
            val requestUri = request.requestURI
            var jwt = jwtUtils.getTokenFromRequest(request)
            if (loginPath == requestUri) {
                jwt = null
            }
            if (jwt != null && StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
                val userId = jwtUtils.getUserIdFromToken(jwt)
                val userDetails = userDetailsServiceImplementation.loadUserById(userId)
                val authenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
                if (requestUri != heartbeatPath) {
                    jwt = jwtUtils.refreshToken(jwt)
                    cookieService.setCookieHeader(response, cookieService.createTokenCookie(jwt))
                }
                val tokenExpiryTime = jwtUtils.getTokenExpirationTime(jwt)
                response.addHeader(
                    "X-SESSION-TTL",
                    ChronoUnit.SECONDS.between(
                        LocalDateTime.now(),
                        tokenExpiryTime.toInstant()
                    ).toString()
                )
            }
        } catch (_: Exception) { }
        filterChain.doFilter(request, response)
    }

    companion object {
        private const val loginPath = "/api/auth/login"
        private const val heartbeatPath = "/api/heartbeat"
    }
}
