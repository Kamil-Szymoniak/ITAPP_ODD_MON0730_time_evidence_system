import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import pl.edu.pwr.timeevidence.config.UserPrincipal

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory::class)
annotation class WithMockCustomUser(
    val id: Int = 1,
    val username: String,
    val personId: Int = 0,
    val email: String = "email@email.email",
    val authorities: Array<String> = []
)

class WithMockCustomUserSecurityContextFactory : WithSecurityContextFactory<WithMockCustomUser> {
    override fun createSecurityContext(customUser: WithMockCustomUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val principal = UserPrincipal(
            customUser.id,
            customUser.username,
            if (customUser.personId == 0) null else customUser.personId,
            customUser.email,
            "password",
            customUser.authorities.map { SimpleGrantedAuthority(it) }
        )
        val auth: Authentication =
            UsernamePasswordAuthenticationToken(principal, "password", principal.authorities)
        context.authentication = auth
        return context
    }
}