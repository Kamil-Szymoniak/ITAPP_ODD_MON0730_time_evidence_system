package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.NullableNotBlank
import pl.edu.pwr.timeevidence.entity.PermissionEntity
import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class PermissionRequest (
    @field:NullableNotBlank
    val name: String,
    val description: String?
)

data class PermissionResponse (
    val id: Short,
    val name: String,
    val description: String?
) {
    companion object {
        fun fromEntity(entity: PermissionEntity) = PermissionResponse(entity.id!!, entity.name, entity.description)
    }
}