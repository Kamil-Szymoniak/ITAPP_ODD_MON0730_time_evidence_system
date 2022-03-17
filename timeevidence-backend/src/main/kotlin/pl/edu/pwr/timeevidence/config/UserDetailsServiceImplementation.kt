package pl.edu.pwr.timeevidence.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.edu.pwr.timeevidence.dao.UserRepository

@Service
class UserDetailsServiceImplementation @Autowired constructor(userRepository: UserRepository) :
    UserDetailsService {
    private val userRepository: UserRepository

    init {
        this.userRepository = userRepository
    }

    @Transactional
    @Throws(UsernameNotFoundException::class, LockedException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val account = userRepository.findUserByUsernameIgnoreCase(username)
            .filter { it.password != "" }
            .orElseThrow { UsernameNotFoundException("No user found with login $username") }
        return UserPrincipal.create(account)
    }

    @Transactional
    @Throws(UsernameNotFoundException::class, LockedException::class)
    fun loadUserById(id: Int): UserDetails {
        val account =
            userRepository.findById(id).orElseThrow { UsernameNotFoundException("No user found with id $id") }
        return UserPrincipal.create(account)
    }
}
