package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.entity.TeamEntity
import javax.validation.constraints.NotBlank

data class TeamRequest(
    @field:NotBlank
    val name: String,
    val description: String?,
    val teamMembers: List<Int>,
    val teamLeader: Int?
)

data class TeamResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val teamMembers: List<DictionaryResponse>,
    val teamLeader: DictionaryResponse?
) {
    companion object {
        fun fromEntity(entity: TeamEntity) = TeamResponse(
            id = entity.id!!,
            name = entity.name,
            description = entity.description,
            teamMembers = entity.teamMembers.map { DictionaryResponse.fromPerson(it) },
            teamLeader = entity.teamLeader?.let { DictionaryResponse.fromPerson(it) }
        )
    }
}