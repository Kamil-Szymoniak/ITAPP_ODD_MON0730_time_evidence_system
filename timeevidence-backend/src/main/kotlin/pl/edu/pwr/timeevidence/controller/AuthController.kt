package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.LoginRequest
import pl.edu.pwr.timeevidence.service.UserService
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    fun login(@Valid @RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<BasicResponse> =
        ResponseEntity.ok(BasicResponse(userService.handleLogin(request, response), true))

    @PostMapping("/logout")
    @PreAuthorize("permitAll()")
    fun logout(response: HttpServletResponse): ResponseEntity<BasicResponse> {
        userService.handleLogout(response)
        return ResponseEntity.ok(BasicResponse("Logged out successfully", true))
    }
}
