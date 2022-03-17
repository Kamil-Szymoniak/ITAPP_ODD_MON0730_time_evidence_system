package pl.edu.pwr.timeevidence.config

import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class CookieService {
    private val cookieName = "TIME_EVIDENCE"
    private val sessionDuration = 1200000 //20 min

    fun createTokenCookie(token: String): ResponseCookie {
        return ResponseCookie.from(cookieName, token)
            .secure(false)
            .httpOnly(true)
            .path("/")
            .maxAge((sessionDuration / 1000).toLong())
            .sameSite("Strict")
            .build()
    }

    fun createRemovalCookie(): ResponseCookie {
        return ResponseCookie.from(cookieName, "")
            .secure(false)
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build()
    }

    fun setCookieHeader(response: HttpServletResponse, cookie: ResponseCookie) {
        response.setHeader("Set-Cookie", cookie.toString())
    }

    fun getCookieFromRequest(request: HttpServletRequest): Cookie? {
        val cookies = request.cookies
        if (cookies != null && cookies.isNotEmpty()) {
            for (cookie in cookies) {
                if (cookieName == cookie.name) {
                    return cookie
                }
            }
        }
        return null
    }
}
