package pl.edu.pwr.timeevidence.service

import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.dao.PermissionRepository
import pl.edu.pwr.timeevidence.dto.DictionaryResponse
import pl.edu.pwr.timeevidence.dto.PagedResponse
import pl.edu.pwr.timeevidence.dto.PermissionRequest
import pl.edu.pwr.timeevidence.dto.PermissionResponse
import pl.edu.pwr.timeevidence.entity.PermissionEntity
import pl.edu.pwr.timeevidence.exception.NotFoundException
import pl.edu.pwr.timeevidence.specification.EntityCriteria

@Service
class PermissionService (private val permissionRepository: PermissionRepository) {
    fun createPermission(request: PermissionRequest) = permissionRepository.save(fromDto(request)).id!!
    fun editPermission(request: PermissionRequest, id: Short) =
        if (!permissionRepository.existsById(id)) throw NotFoundException("Permission", "id", id)
        else PermissionResponse.fromEntity(permissionRepository.save(fromDto(request, id)))
    fun getPermission(id: Short) = PermissionResponse.fromEntity(permissionRepository.findById(id)
        .orElseThrow { throw NotFoundException("Permission", "id", id) })
    fun getPermissions(criteria: EntityCriteria<PermissionEntity>) =
        PagedResponse(permissionRepository.findAll(criteria.specification, criteria.paging!!).map { PermissionResponse.fromEntity(it) })
    fun getAllPermissions() = permissionRepository.findAll().map { DictionaryResponse.fromPermission(it) }.toList()
    fun deletePermission(id: Short) {
        if (permissionRepository.findById(id).isEmpty) {
            throw NotFoundException("Permission", "id", id)
        }
        permissionRepository.deleteById(id)
    }

    fun fromDto(request: PermissionRequest) = PermissionEntity(
        name = request.name,
        description = request.description
    )

    fun fromDto(request: PermissionRequest, id: Short) = PermissionEntity(
        id = id,
        name = request.name,
        description = request.description
    )
}