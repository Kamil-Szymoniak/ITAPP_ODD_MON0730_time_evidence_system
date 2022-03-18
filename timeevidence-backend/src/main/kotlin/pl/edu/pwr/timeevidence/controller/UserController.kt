package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.UserRequest
import pl.edu.pwr.timeevidence.service.UserService
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_USERS', 'CAN_EDIT_USERS')")
    fun editUser(@Valid @RequestBody request: UserRequest, @PathVariable id: Int) = ResponseEntity
        .ok(BasicResponse("User edited successfully", true, userService.editUser(request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_SEE_USERS')")
    fun getUser(@PathVariable id: Int) = ResponseEntity.ok(userService.getUser(id))
    @GetMapping("/me")
    @PreAuthorize("permitAll()")
    fun getMe(auth: Authentication) = ResponseEntity.ok(userService.getMe(auth))
    //@GetMapping
    //@PreAuthorize("hasAuthority('CAN_SEE_USERS')")
    //@SearchRequest(specification = UserSpecification::class)
    //fun getUsers(criteria: EntityCriteria<UserEntity>) = ResponseEntity.ok(userService.getUsers(criteria))
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CAN_SEE_USERS')")
    fun getAllUsers() = ResponseEntity.ok(userService.getAllUsers())
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_USERS', 'CAN_EDIT_USERS')")
    fun deleteUser(@PathVariable id: Int): ResponseEntity<BasicResponse> {
        userService.deleteUser(id)
        return ResponseEntity.ok(BasicResponse("User deleted successfully", true))
    }
}
