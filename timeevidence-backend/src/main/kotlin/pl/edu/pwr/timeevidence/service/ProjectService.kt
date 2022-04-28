package pl.edu.pwr.timeevidence.service

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.config.UserPrincipal
import pl.edu.pwr.timeevidence.dao.PersonRepository
import pl.edu.pwr.timeevidence.dao.ProjectRepository
import pl.edu.pwr.timeevidence.dto.DictionaryResponse
import pl.edu.pwr.timeevidence.dto.PagedResponse
import pl.edu.pwr.timeevidence.dto.ProjectRequest
import pl.edu.pwr.timeevidence.dto.ProjectResponse
import pl.edu.pwr.timeevidence.entity.ProjectEntity
import pl.edu.pwr.timeevidence.exception.BadRequestException
import pl.edu.pwr.timeevidence.exception.NotFoundException
import pl.edu.pwr.timeevidence.specification.EntityCriteria

@Service
class ProjectService (private val projectRepository: ProjectRepository, private val personRepository: PersonRepository) {
    fun createProject(request: ProjectRequest) = projectRepository.save(fromDto(request)).id!!
    fun editProject(request: ProjectRequest, id: Int) =
        if (!projectRepository.existsById(id)) throw NotFoundException("Project", "id", id)
        else ProjectResponse.fromEntity(projectRepository.save(fromDto(request, id)))
    fun getProject(id: Int) = ProjectResponse.fromEntity(projectRepository.findById(id)
        .orElseThrow { throw NotFoundException("Project", "id", id) })
    fun getProjects(criteria: EntityCriteria<ProjectEntity>) =
        PagedResponse(projectRepository.findAll(criteria.specification, criteria.paging!!).map { ProjectResponse.fromEntity(it) })
    fun getAllProjects() = projectRepository.findAll().map { DictionaryResponse.fromProject(it) }
    fun getMyProjects(auth: Authentication): List<DictionaryResponse> {
        (auth.principal as UserPrincipal).getPersonId().let {
            if (it == null) throw BadRequestException("User does not have a person assigned to the account")
            val person = personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
            return person.projects.map { project ->
                DictionaryResponse.fromProject(project)
            }
        }
    }
    fun deleteProject(id: Int) {
        if (projectRepository.findById(id).isEmpty) {
            throw NotFoundException("Project", "id", id)
        }
        projectRepository.deleteById(id)
    }

    fun fromDto(request: ProjectRequest): ProjectEntity {
        val projectManager = request.projectManager?.let {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }
        val projectMembers = request.projectMembers.map {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }.toMutableList()
        if (projectManager != null && projectMembers.firstOrNull { it.id == projectManager.id } == null) {
            projectMembers.add(projectManager)
        }

        return ProjectEntity(
            name = request.name,
            inhouseName = request.inhouseName,
            description = request.description,
            clientName = request.clientName,
            beginningDate = request.beginningDate,
            projectMembers = projectMembers,
            projectManager = projectManager
        )
    }

    fun fromDto(request: ProjectRequest, id: Int): ProjectEntity {
        val projectManager = request.projectManager?.let {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }
        val projectMembers = request.projectMembers.map {
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        }.toMutableList()
        if (projectManager != null && projectMembers.firstOrNull { it.id == projectManager.id } == null) {
            projectMembers.add(projectManager)
        }

        return ProjectEntity(
            id = id,
            name = request.name,
            inhouseName = request.inhouseName,
            description = request.description,
            clientName = request.clientName,
            beginningDate = request.beginningDate,
            projectMembers = projectMembers,
            projectManager = projectManager
        )
    }
}