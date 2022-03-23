package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.ChangePasswordRequest
import pl.edu.pwr.timeevidence.dto.LoginRequest
import pl.edu.pwr.timeevidence.dto.UserRequest
import pl.edu.pwr.timeevidence.service.UserService
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    //@PreAuthorize("hasAuthority('CAN_EDIT_USERS')")
    fun register(@RequestBody request: @Valid UserRequest): ResponseEntity<BasicResponse> =
        ResponseEntity.created(UriBuilder.getUri("/users/{id}", userService.handleRegister(request)))
            .body(BasicResponse("User added successfully", true))

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    fun login(@RequestBody request: @Valid LoginRequest, response: HttpServletResponse): ResponseEntity<BasicResponse> =
        ResponseEntity.ok(BasicResponse(userService.handleLogin(request, response), true))

    @PostMapping("/logout")
    @PreAuthorize("permitAll()")
    fun logout(response: HttpServletResponse): ResponseEntity<BasicResponse> {
        userService.handleLogout(response)
        return ResponseEntity.ok(BasicResponse("Logged out successfully", true))
    }
    @PutMapping("/me/password")
    @PreAuthorize("permitAll()")
    fun editMePassword(@RequestBody request: @Valid ChangePasswordRequest,
                       auth: Authentication,
                       response: HttpServletResponse
    ): ResponseEntity<BasicResponse> {
        userService.editMePassword(request, auth, response)
        return ResponseEntity.ok(BasicResponse("Password changed", true))
    }
}
