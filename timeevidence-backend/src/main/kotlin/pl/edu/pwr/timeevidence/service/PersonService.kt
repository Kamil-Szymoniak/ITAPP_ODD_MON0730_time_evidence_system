package pl.edu.pwr.timeevidence.service

import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.dao.PersonRepository
import pl.edu.pwr.timeevidence.dao.ProjectRepository
import pl.edu.pwr.timeevidence.dao.TeamRepository
import pl.edu.pwr.timeevidence.dto.DictionaryResponse
import pl.edu.pwr.timeevidence.dto.PagedResponse
import pl.edu.pwr.timeevidence.dto.PersonRequest
import pl.edu.pwr.timeevidence.dto.PersonResponse
import pl.edu.pwr.timeevidence.entity.PersonEntity
import pl.edu.pwr.timeevidence.entity.ProjectEntity
import pl.edu.pwr.timeevidence.entity.TeamEntity
import pl.edu.pwr.timeevidence.exception.NotFoundException
import pl.edu.pwr.timeevidence.specification.EntityCriteria

@Service
class PersonService (private val personRepository: PersonRepository,
                     private val projectRepository: ProjectRepository,
                     private val teamRepository: TeamRepository) {

    fun createPerson(request: PersonRequest) = personRepository.save(fromDto(request)).id!!
    fun editPerson(request: PersonRequest, id: Int) =
        if (!personRepository.existsById(id)) throw NotFoundException("Person", "id", id)
        else PersonResponse.fromEntity(personRepository.save(fromDto(request, id)))
    fun getPerson(id: Int) = PersonResponse.fromEntity(personRepository.findById(id)
        .orElseThrow { throw NotFoundException("Person", "id", id) })
    fun getPersonsInProject(projectId: Int, criteria: EntityCriteria<PersonEntity>): PagedResponse<PersonResponse> {
        if (!projectRepository.existsById(projectId)) throw NotFoundException("Project", "id", projectId)
        var spec = Specification<PersonEntity> { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.join<PersonEntity, List<ProjectEntity>>("projects").get<Int>("id"), projectId)
        }
        if (criteria.specification != null) {
            spec = spec.and(criteria.specification)
        }
        return PagedResponse(personRepository.findAll(spec, criteria.paging!!).map { PersonResponse.fromEntity(it) })
    }
    fun getPersonsInTeam(teamId: Int, criteria: EntityCriteria<PersonEntity>): PagedResponse<PersonResponse> {
        if (!teamRepository.existsById(teamId)) throw NotFoundException("Team", "id", teamId)
        var spec = Specification<PersonEntity> { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.join<PersonEntity, List<TeamEntity>>("teams").get<Int>("id"), teamId)
        }
        if (criteria.specification != null) {
            spec = spec.and(criteria.specification)
        }
        return PagedResponse(personRepository.findAll(spec, criteria.paging!!).map { PersonResponse.fromEntity(it) })
    }
    fun getPersons(criteria: EntityCriteria<PersonEntity>) =
        PagedResponse(personRepository.findAll(criteria.specification, criteria.paging!!).map { PersonResponse.fromEntity(it) })
    fun getAllPersons() = personRepository.findAll().map { DictionaryResponse.fromPerson(it) }.toList()
    fun deletePerson(id: Int) {
        if (personRepository.findById(id).isEmpty) {
            throw NotFoundException("Person", "id", id)
        }
        personRepository.deleteById(id)
    }

    fun fromDto(request: PersonRequest) = PersonEntity(
        name = request.name,
        surname = request.surname,
        phone = request.phone,
        birthday = request.birthday
    )

    fun fromDto(request: PersonRequest, id: Int) = PersonEntity(
        id = id,
        name = request.name,
        surname = request.surname,
        phone = request.phone,
        birthday = request.birthday
    )
}