package pl.edu.pwr.timeevidence.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import pl.edu.pwr.timeevidence.entity.UserEntity

class UserPrincipal (
    private val id: Int,
    private val username: String,
    private val email: String,
    private val password: String,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {
    companion object {
        fun create(user: UserEntity): UserPrincipal {
            val authorities = user.roles
                .map { it.permissions }
                .flatten()
                .map { SimpleGrantedAuthority(it.name) }
                .toSet()
            return UserPrincipal(
                user.id!!,
                user.username,
                user.email,
                user.password,
                authorities
            )
        }
    }

    fun getId() = id
    override fun getUsername() = username
    fun getEmail() = email
    override fun getAuthorities() = authorities
    override fun getPassword() = password
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserPrincipal

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (authorities != other.authorities) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + authorities.hashCode()
        return result
    }
}