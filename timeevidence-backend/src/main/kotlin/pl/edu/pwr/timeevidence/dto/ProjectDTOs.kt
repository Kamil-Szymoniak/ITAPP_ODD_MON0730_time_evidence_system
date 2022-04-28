package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.entity.ProjectEntity
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class ProjectRequest(
    @field:NotBlank
    val name: String,
    val inhouseName: String?,
    val description: String?,
    @field:NotBlank
    val clientName: String,
    val beginningDate: LocalDate,
    val projectMembers: List<Int>,
    val projectManager: Int?
)

data class ProjectResponse(
    val id: Int,
    val name: String,
    val inhouseName: String?,
    val description: String?,
    val clientName: String,
    val beginningDate: LocalDate,
    val projectMembers: List<DictionaryResponse>,
    val projectManager: DictionaryResponse?
) {
    companion object {
        fun fromEntity(entity: ProjectEntity) = ProjectResponse(
            id = entity.id!!,
            name = entity.name,
            inhouseName = entity.inhouseName,
            description = entity.description,
            clientName = entity.clientName,
            beginningDate = entity.beginningDate,
            projectMembers = entity.projectMembers.map { DictionaryResponse.fromPerson(it) },
            projectManager = entity.projectManager?.let { DictionaryResponse.fromPerson(it) }
        )
    }
}