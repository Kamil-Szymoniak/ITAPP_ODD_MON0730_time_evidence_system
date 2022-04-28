package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.PermissionRequest
import pl.edu.pwr.timeevidence.entity.PermissionEntity
import pl.edu.pwr.timeevidence.service.PermissionService
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import pl.edu.pwr.timeevidence.specification.QueryRequest
import pl.edu.pwr.timeevidence.specification.entity.PermissionSpecification
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/permissions")
class PermissionController(private val permissionService: PermissionService) {
    @PostMapping
    @PreAuthorize("hasAuthority('CAN_EDIT_PERMISSIONS')")
    fun createPermission(@Valid @RequestBody request: PermissionRequest) = ResponseEntity
        .created(UriBuilder.getUri("/permissions/{id}", permissionService.createPermission(request)))
        .body(BasicResponse("Permission added successfully", true))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_PERMISSIONS')")
    fun editPermission(@Valid @RequestBody request: PermissionRequest, @PathVariable id: Short) = ResponseEntity
        .ok(BasicResponse("Permission edited successfully", true, permissionService.editPermission(request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERMISSIONS', 'CAN_EDIT_PERMISSIONS')")
    fun getPermission(@PathVariable id: Short) = ResponseEntity.ok(permissionService.getPermission(id))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERMISSIONS', 'CAN_EDIT_PERMISSIONS')")
    @QueryRequest(specification = PermissionSpecification::class)
    fun getPermissions(criteria: EntityCriteria<PermissionEntity>) = ResponseEntity.ok(permissionService.getPermissions(criteria))
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERMISSIONS', 'CAN_EDIT_PERMISSIONS')")
    fun getAllPermissions() = ResponseEntity.ok(permissionService.getAllPermissions())
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_PERMISSIONS')")
    fun deletePermission(@PathVariable id: Short): ResponseEntity<BasicResponse> {
        permissionService.deletePermission(id)
        return ResponseEntity.ok(BasicResponse("Permission deleted successfully", true))
    }
}
