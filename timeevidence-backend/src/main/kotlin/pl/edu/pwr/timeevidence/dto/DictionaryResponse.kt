package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.entity.*

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
        fun fromUser(entity: UserEntity) = DictionaryResponse(
            id = entity.id!!,
            name = entity.username,
            description = entity.email
        )
        fun fromPerson(entity: PersonEntity) = DictionaryResponse(
            id = entity.id!!,
            name = "${entity.name} ${entity.surname}",
            description = "${entity.phone}"
        )

        fun fromTeam(entity: TeamEntity) = DictionaryResponse(
            id = entity.id!!,
            name = entity.name,
            description = "Team leader: ${entity.teamLeader?.let { "${it.name} ${it.surname}" } ?: "none"},\nNumber of team members: ${entity.teamMembers.size}"
        )

        fun fromProject(entity: ProjectEntity) = DictionaryResponse(
            id = entity.id!!,
            name = entity.name,
            description = "Client name: ${entity.clientName},\nProject manager: ${entity.projectManager?.let { "${it.name} ${it.surname}" } ?: "none"},\nNumber of project members: ${entity.projectMembers.size}"
        )

        fun fromTimeEvidence(entity: TimeEvidenceEntity) = DictionaryResponse(
            id = entity.id!!,
            name = "${entity.person!!.name} ${entity.person!!.surname} - ${entity.date}",
            description = "Project: ${entity.project!!.name},\nNumber of minutes: ${entity.minutes},\nComment: ${entity.comment ?: "none"}"
        )

        fun fromAvailability(entity: AvailabilityEntity) = DictionaryResponse(
            id = entity.id!!,
            name = "${entity.person!!.name} ${entity.person!!.surname} - ${entity.date}",
            description = "Team: ${entity.team!!.name},\n${entity.periods.split("_").map { it.split("-").joinToString(" - ") }.joinToString { "\n" }}"
        )
    }
}