package pl.edu.pwr.timeevidence.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.config.CookieService
import pl.edu.pwr.timeevidence.config.JwtUtils
import pl.edu.pwr.timeevidence.config.UserPrincipal
import pl.edu.pwr.timeevidence.dao.RoleRepository
import pl.edu.pwr.timeevidence.dao.UserRepository
import pl.edu.pwr.timeevidence.dto.*
import pl.edu.pwr.timeevidence.entity.UserEntity
import pl.edu.pwr.timeevidence.exception.BadRequestException
import pl.edu.pwr.timeevidence.exception.NotFoundException
import javax.servlet.http.HttpServletResponse

@Service
class UserService (
    val jwtUtils: JwtUtils,
    val authenticationManager: AuthenticationManager,
    val cookieService: CookieService,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun handleRegister(request: UserRequest): Int {
        if (userRepository.existsByUsernameIgnoreCase(request.username)) {
            throw BadRequestException("User with this username already exists")
        }
        if (userRepository.existsByEmailIgnoreCase(request.email)) {
            throw BadRequestException("This email is already in use")
        }
        return userRepository.save(fromDto(request)).id!!
    }

    fun handleLogin(request: LoginRequest, response: HttpServletResponse): String {
        val username = userRepository
            .findByEmailIgnoreCase(request.login)
            .map { it.username }
            .orElse(request.login)
        val token = UsernamePasswordAuthenticationToken(username, request.password)
        val auth = authenticationManager.authenticate(token)
        SecurityContextHolder.getContext().authentication = auth
        val jwt = jwtUtils.generateToken(auth)
        cookieService.setCookieHeader(response, cookieService.createTokenCookie(jwt))
        return jwt
    }

    fun handleLogout(response: HttpServletResponse) {
        cookieService.setCookieHeader(response, cookieService.createRemovalCookie())
    }

    fun editMePassword(request: ChangePasswordRequest, auth: Authentication, response: HttpServletResponse) {
        val me = userRepository.findUserByUsernameIgnoreCase((auth.principal as UserPrincipal).username)
            .orElseThrow { NotFoundException("Me", "username", (auth.principal as UserPrincipal).username) }
        var token = UsernamePasswordAuthenticationToken(me.username, request.oldPassword)
        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(token)
        me.password = passwordEncoder.encode(request.newPassword)
        userRepository.save(me)
        token = UsernamePasswordAuthenticationToken(me.username, request.newPassword)
        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(token)
        val jwt = jwtUtils.generateToken(auth)
        cookieService.setCookieHeader(response, cookieService.createTokenCookie(jwt))
    }

    fun editUser(request: UserRequest, id: Int): UserResponse {
        val user = userRepository.findById(id).orElseThrow { NotFoundException("User", "id", id) }
        if (userRepository.findUserByUsernameIgnoreCase(request.username).filter{ it.id != user.id }.isPresent ) {
            throw BadRequestException("User with this name already exists")
        }
        if (userRepository.findByEmailIgnoreCase(request.email).filter{ it.id != user.id }.isPresent ) {
            throw BadRequestException("User with this email already exists")
        }
        user.username = request.username
        user.email = request.email
        user.roles = request.roles.map { roleRepository.findById(it).orElseThrow { NotFoundException("Role", "id", it) } }
        return UserResponse.fromEntity(userRepository.save(user))
    }

    fun getUser(id: Int) =
        UserResponse.fromEntity(userRepository.findById(id).orElseThrow { NotFoundException("User", "id", id) })

    fun getMe(auth: Authentication) =
        UserResponse.fromEntity(userRepository.findUserByUsernameIgnoreCase((auth.principal as UserPrincipal).username)
            .orElseThrow { NotFoundException("Me", "username", (auth.principal as UserPrincipal).username) })

//   fun getUsers(criteria: EntityCriteria<UserEntity>) =
//        PagedResponse(userRepository.findAll(criteria.specification, criteria.paging!!).map { UserResponse(it) })

    fun getAllUsers() = userRepository.findAll().map { DictionaryResponse.fromUser(it) }
    
    fun deleteUser(id: Int) {
        if (userRepository.findById(id).isEmpty) {
            throw NotFoundException("User", "id", id)
        }
        userRepository.deleteById(id)
    }

    fun fromDto(dto: UserRequest) = UserEntity(
        username = dto.username,
        email = dto.email,
        password = passwordEncoder.encode(dto.password),
        roles = dto.roles.map { roleRepository.findById(it).orElseThrow { NotFoundException("Role", "id", it) } }
    )
}