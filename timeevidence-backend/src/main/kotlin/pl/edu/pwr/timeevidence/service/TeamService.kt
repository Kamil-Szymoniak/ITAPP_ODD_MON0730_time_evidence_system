package pl.edu.pwr.timeevidence.service

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.config.UserPrincipal
import pl.edu.pwr.timeevidence.dao.PersonRepository
import pl.edu.pwr.timeevidence.dao.TeamRepository
import pl.edu.pwr.timeevidence.dto.*
import pl.edu.pwr.timeevidence.entity.TeamEntity
import pl.edu.pwr.timeevidence.exception.BadRequestException
import pl.edu.pwr.timeevidence.exception.NotFoundException
import pl.edu.pwr.timeevidence.specification.EntityCriteria

@Service
class TeamService (private val teamRepository: TeamRepository, private val personRepository: PersonRepository) {
    fun createTeam(request: TeamRequest) = teamRepository.save(fromDto(request)).id!!
    fun editTeam(request: TeamRequest, id: Int) =
        if (!teamRepository.existsById(id)) throw NotFoundException("Team", "id", id)
        else TeamResponse.fromEntity(teamRepository.save(fromDto(request, id)))
    fun getTeam(id: Int) = TeamResponse.fromEntity(teamRepository.findById(id)
        .orElseThrow { throw NotFoundException("Team", "id", id) })
    fun getTeams(criteria: EntityCriteria<TeamEntity>) =
        PagedResponse(teamRepository.findAll(criteria.specification, criteria.paging!!).map { TeamResponse.fromEntity(it) })
    fun getAllTeams() = teamRepository.findAll().map { DictionaryResponse.fromTeam(it) }
    fun getMyTeams(auth: Authentication): List<DictionaryResponse> {
        (auth.principal as UserPrincipal).getPersonId().let {
            if (it == null) throw BadRequestException("User does not have a person assigned to the account")
            val person = personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
            return person.teams.map { team ->
                DictionaryResponse.fromTeam(team)
            }
        }

    }
    fun deleteTeam(id: Int) {
        if (teamRepository.findById(id).isEmpty) {
            throw NotFoundException("Team", "id", id)
        }
        teamRepository.deleteById(id)
    }

    fun fromDto(request: TeamRequest): TeamEntity {
        val teamLeader = request.teamLeader?.let {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }
        val teamMembers = request.teamMembers.map {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }.toMutableList()
        if (teamLeader != null && teamMembers.firstOrNull { it.id == teamLeader.id } == null) {
            teamMembers.add(teamLeader)
        }

        return TeamEntity(
            name = request.name,
            description = request.description,
            teamMembers = teamMembers,
            teamLeader = teamLeader
        )
    }

    fun fromDto(request: TeamRequest, id: Int): TeamEntity {
        val teamLeader = request.teamLeader?.let {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }
        val teamMembers = request.teamMembers.map {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }.toMutableList()
        if (teamLeader != null && teamMembers.firstOrNull { it.id == teamLeader.id } == null) {
            teamMembers.add(teamLeader)
        }

        return TeamEntity(
            id = id,
            name = request.name,
            description = request.description,
            teamMembers = teamMembers,
            teamLeader = teamLeader
        )
    }
}