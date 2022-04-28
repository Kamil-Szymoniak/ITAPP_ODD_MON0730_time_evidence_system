package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.ChangePasswordRequest
import pl.edu.pwr.timeevidence.dto.UserRequest
import pl.edu.pwr.timeevidence.entity.UserEntity
import pl.edu.pwr.timeevidence.service.UserService
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import pl.edu.pwr.timeevidence.specification.QueryRequest
import pl.edu.pwr.timeevidence.specification.entity.UserSpecification
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_USERS')")
    fun editUser(@Valid @RequestBody request: UserRequest, @PathVariable id: Int) = ResponseEntity
        .ok(BasicResponse("User edited successfully", true, userService.editUser(request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_USERS', 'CAN_EDIT_USERS')")
    fun getUser(@PathVariable id: Int) = ResponseEntity.ok(userService.getUser(id))
    @GetMapping("/me")
    @PreAuthorize("permitAll()")
    fun getMe(auth: Authentication) = ResponseEntity.ok(userService.getMe(auth))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_USERS', 'CAN_EDIT_USERS')")
    @QueryRequest(specification = UserSpecification::class)
    fun getUsers(criteria: EntityCriteria<UserEntity>) = ResponseEntity.ok(userService.getUsers(criteria))
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_USERS', 'CAN_EDIT_USERS')")
    fun getAllUsers() = ResponseEntity.ok(userService.getAllUsers())
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_USERS')")
    fun deleteUser(@PathVariable id: Int): ResponseEntity<BasicResponse> {
        userService.deleteUser(id)
        return ResponseEntity.ok(BasicResponse("User deleted successfully", true))
    }
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('CAN_EDIT_USERS')")
    fun register(@Valid @RequestBody request: UserRequest): ResponseEntity<BasicResponse> =
        ResponseEntity.created(UriBuilder.getUri("/users/{id}", userService.handleRegister(request)))
            .body(BasicResponse("User added successfully", true))
    @PutMapping("/me/password")
    @PreAuthorize("permitAll()")
    fun editMyPassword(@Valid @RequestBody request: ChangePasswordRequest, auth: Authentication, response: HttpServletResponse):
            ResponseEntity<BasicResponse> {
        userService.editMePassword(request, auth, response)
        return ResponseEntity.ok(BasicResponse("Password changed", true))
    }
}
