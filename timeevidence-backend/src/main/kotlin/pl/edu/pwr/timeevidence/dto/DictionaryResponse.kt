package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.entity.PermissionEntity
import pl.edu.pwr.timeevidence.entity.RoleEntity

class DictionaryResponse (
    val id: Number, val name: String, val description: String?
        ) {
    companion object {
        fun fromPermission(entity: PermissionEntity) = DictionaryResponse(
            id = entity.id ?: -1,
            name = entity.name,
            description = entity.description
        )
        fun fromRole(entity: RoleEntity) = DictionaryResponse(
            id = entity.id!!,
            name = entity.name,
            description = entity.description
        )
    }
}