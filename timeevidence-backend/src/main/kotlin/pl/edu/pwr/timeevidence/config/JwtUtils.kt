package pl.edu.pwr.timeevidence.config

import io.jsonwebtoken.*
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import javax.xml.bind.DatatypeConverter

@Component
class JwtUtils (
    //TODO private val cookieService: CookieService,
) {
    private val jwtSecret: String = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecret"
    private val jwtExpirationTime: Int = 1200000 //20 min
    private val signingKey: Key = SecretKeySpec(DatatypeConverter.parseBase64Binary(jwtSecret), SignatureAlgorithm.HS512.jcaName)

    fun generateToken(auth: Authentication) = Jwts.builder()
        .setIssuer("Kamil Szymoniak")
        .setSubject((auth.principal as /*TODO UserPrincipal*/).getId().toString())
        .setIssuedAt(Date())
        .setExpiration(Date(Date().time + jwtExpirationTime))
        .setId(UUID.randomUUID().toString())
        .signWith(signingKey)
        .compact()!!

    fun getTokenFromRequest(request: HttpServletRequest) = cookieService.getCookieFromRequest(request)?.value
    fun getUserIdFromToken(token: String) =
        Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).body.subject.toInt()
    fun refreshToken(originalToken: String): String {
        val claims = HashMap<String, Any>()
        val headerClaims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(originalToken)
        claims["sessionId"] = headerClaims.body["sessionId"]!!
        claims["authorities"] = headerClaims.body["authorities"]!!
        claims["level"] = headerClaims.body["level"]!!
        if (headerClaims.body["originalSub"] != null) {
            claims["originalSub"] = headerClaims.body["originalSub"]!!
        }
        return Jwts.builder()
            .setIssuer("Wrocław University of Science and Technology")
            .setClaims(claims)
            .setSubject(headerClaims.body.subject)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationTime))
            .setId(UUID.randomUUID().toString())
            .signWith(signingKey)
            .compact()
    }
    fun getTokenExpirationTime(token: String): Date =
        Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).body.expiration

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token)
            return true
        } catch (_: SecurityException) {
        } catch (_: MalformedJwtException) {
        } catch (_: ExpiredJwtException) {
        } catch (_: UnsupportedJwtException) {
        } catch (_: IllegalArgumentException) {
        }
        return false
    }
}