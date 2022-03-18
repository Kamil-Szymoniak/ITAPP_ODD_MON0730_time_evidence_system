package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.entity.RoleEntity
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class RoleRequest (
    @field:NotBlank
    val name: String,
    val description: String?,
    @field:NotEmpty
    val permissions: List<Short>
)

data class RoleResponse (
    val id: Short,
    val name: String,
    val description: String?,
    val permissions: List<DictionaryResponse>
) {
    companion object {
        fun fromEntity(entity: RoleEntity) = RoleResponse(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            permissions = entity.permissions.map { DictionaryResponse.fromPermission(it) }
        )
    }
}