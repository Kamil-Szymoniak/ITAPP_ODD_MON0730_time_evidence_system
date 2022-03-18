package pl.edu.pwr.timeevidence.service

import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.dao.PermissionRepository
import pl.edu.pwr.timeevidence.dao.RoleRepository
import pl.edu.pwr.timeevidence.dto.DictionaryResponse
import pl.edu.pwr.timeevidence.dto.RoleRequest
import pl.edu.pwr.timeevidence.dto.RoleResponse
import pl.edu.pwr.timeevidence.entity.RoleEntity
import pl.edu.pwr.timeevidence.exception.NotFoundException

@Service
class RoleService (private val roleRepository: RoleRepository, private val permissionRepository: PermissionRepository) {
    fun createRole(request: RoleRequest) = roleRepository.save(fromDto(request)).id!!
    fun editRole(request: RoleRequest, id: Short) =
        if (!roleRepository.existsById(id)) throw NotFoundException("Role", "id", id)
        else RoleResponse.fromEntity(roleRepository.save(fromDto(request, id)))
    fun getRole(id: Short) = RoleResponse.fromEntity(roleRepository.findById(id)
        .orElseThrow { throw NotFoundException("Role", "id", id) })
    //fun getRoles(criteria: EntityCriteria<RoleEntity>) =
    //    PagedResponse(roleRepository.findAll(criteria.specification, criteria.paging!!).map { RoleResponse(it) })
    fun getAllRoles() = roleRepository.findAll().map { DictionaryResponse.fromRole(it) }
    fun deleteRole(id: Short) {
        if (roleRepository.findById(id).isEmpty) {
            throw NotFoundException("Role", "id", id)
        }
        roleRepository.deleteById(id)
    }

    fun fromDto(request: RoleRequest) = RoleEntity(
        name = request.name,
        description = request.description,
        permissions = request.permissions.map {
            permissionRepository.findById(it).orElseThrow {
                NotFoundException("Permission", "id", it)
            }
        }
    )

    fun fromDto(request: RoleRequest, id: Short) = RoleEntity(
        id = id,
        name = request.name,
        description = request.description,
        permissions = request.permissions.map {
            permissionRepository.findById(it).orElseThrow {
                NotFoundException("Permission", "id", it)
            }
        }
    )
}