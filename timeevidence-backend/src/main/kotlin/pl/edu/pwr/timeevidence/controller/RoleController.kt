package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.RoleRequest
import pl.edu.pwr.timeevidence.service.RoleService
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/roles")
class RoleController(private val roleService: RoleService) {
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_ROLES', 'CAN_EDIT_ROLES')")
    fun createRole(@Valid @RequestBody request: RoleRequest) = ResponseEntity
        .created(UriBuilder.getUri("/roles/{id}", roleService.createRole(request)))
        .body(BasicResponse("Role added successfully", true))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_ROLES', 'CAN_EDIT_ROLES')")
    fun editRole(@Valid @RequestBody request: RoleRequest, @PathVariable id: Short) = ResponseEntity
        .ok(BasicResponse("Role edited successfully", true, roleService.editRole(request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_SEE_ROLES')")
    fun getRole(@PathVariable id: Short) = ResponseEntity.ok(roleService.getRole(id))
    //@GetMapping
    //@PreAuthorize("hasAuthority('CAN_SEE_ROLES')")
    //@SearchRequest(specification = RoleSpecification::class)
    //fun getRoles(criteria: EntityCriteria<RoleEntity>) = ResponseEntity.ok(roleService.getRoles(criteria))
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CAN_SEE_ROLES')")
    fun getAllRoles() = ResponseEntity.ok(roleService.getAllRoles())
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_ROLES', 'CAN_EDIT_ROLES')")
    fun deleteRole(@PathVariable id: Short): ResponseEntity<BasicResponse> {
        roleService.deleteRole(id)
        return ResponseEntity.ok(BasicResponse("Role deleted successfully", true))
    }
}
