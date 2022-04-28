package pl.edu.pwr.timeevidence.service

import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.config.UserPrincipal
import pl.edu.pwr.timeevidence.dao.PersonRepository
import pl.edu.pwr.timeevidence.dao.ProjectRepository
import pl.edu.pwr.timeevidence.dao.TimeEvidenceRepository
import pl.edu.pwr.timeevidence.dto.*
import pl.edu.pwr.timeevidence.entity.PersonEntity
import pl.edu.pwr.timeevidence.entity.ProjectEntity
import pl.edu.pwr.timeevidence.entity.Status
import pl.edu.pwr.timeevidence.entity.TimeEvidenceEntity
import pl.edu.pwr.timeevidence.exception.BadRequestException
import pl.edu.pwr.timeevidence.exception.ForbiddenException
import pl.edu.pwr.timeevidence.exception.NotFoundException
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import java.time.LocalDate
import java.time.YearMonth

@Service
class TimeEvidenceService(
    private val timeEvidenceRepository: TimeEvidenceRepository,
    private val projectRepository: ProjectRepository,
    private val personRepository: PersonRepository
) {
    fun createTimeEvidence(request: TimeEvidenceRequest, auth: Authentication): Int {
        val project = request.project.let {
            projectRepository.findById(it).orElseThrow {
                NotFoundException("Project", "id", it)
            }
        }
        if (project.projectMembers.firstOrNull { it.id == (auth.principal as UserPrincipal).getPersonId() } == null) {
            throw BadRequestException("Person is not assigned to the project")
        }
        return timeEvidenceRepository.save(fromDto(request, project, auth)).id!!
    }
    fun editTimeEvidence(request: TimeEvidenceRequest, id: Int, auth: Authentication): TimeEvidenceResponse {
        return timeEvidenceRepository.findById(id).orElseThrow {
            NotFoundException("Time evidence", "id", id)
        }.let {
            if (it.status == Status.ACCEPTED.name) {
                throw BadRequestException("Cannot edit accepted request")
            }
            if (it.person!!.id!! != (auth.principal as UserPrincipal).getPersonId()) {
                throw ForbiddenException("User cannot edit someone else's time evidence")
            }
            TimeEvidenceResponse.fromEntity(timeEvidenceRepository.save(fromDto(request, it, auth)))
        }
    }
    fun changeTimeEvidenceStatus(request: TimeEvidenceChangeStatusRequest, id: Int, auth: Authentication) {
        timeEvidenceRepository.findById(id).orElseThrow {
            NotFoundException("Time evidence", "id", id)
        }.let {
            it.project!!.projectManager.let { pm ->
                if (pm == null || pm.id != (auth.principal as UserPrincipal).getPersonId()) {
                    throw ForbiddenException("User is not the pm of this project")
                }
            }
            if (it.status != Status.SENT.name) {
                throw BadRequestException("You can only edit time evidence with status: Sent")
            }
            it.status = Status.valueOf(request.status).name
            it.statusComment = request.statusComment
            timeEvidenceRepository.save(it)
        }
    }
    fun getTimeEvidence(id: Int) = TimeEvidenceResponse.fromEntity(timeEvidenceRepository.findById(id)
        .orElseThrow { throw NotFoundException("Time evidence", "id", id) })
    fun getTimeEvidenceInAMonth(auth: Authentication, monthIndex: Int): HashMap<Int, Int> {
        if (monthIndex < 1 || monthIndex > 12) {
            throw BadRequestException("Month index should be between 1 and 12")
        }
        val currentYear = LocalDate.now().year
        val numberOfDays = YearMonth.of(currentYear, monthIndex).lengthOfMonth()
        val localDateMin = LocalDate.of(currentYear, monthIndex, 1)
        val localDateMax = LocalDate.of(currentYear, monthIndex, numberOfDays)
        val spec = Specification<TimeEvidenceEntity> { root, _, criteriaBuilder ->
            criteriaBuilder.between(root.get("date"), localDateMin, localDateMax)
        }.and {root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<PersonEntity>("person").get<Int>("id"), (auth.principal as UserPrincipal).getPersonId())
        }
        val map = HashMap<Int, Int>()
        for (i in 1..numberOfDays) {
            map[i] = 0
        }
        timeEvidenceRepository.findAll(spec).forEach {
            map[it.date.dayOfMonth] = map[it.date.dayOfMonth]!! + it.minutes
        }
        return map
    }
    fun getUserTimeEvidence(auth: Authentication, criteria: EntityCriteria<TimeEvidenceEntity>): PagedResponse<TimeEvidenceResponse> {
        var spec = Specification<TimeEvidenceEntity> {root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<PersonEntity>("person").get<Int>("id"), (auth.principal as UserPrincipal).getPersonId())
        }
        if (criteria.specification != null) {
            spec = spec.and(criteria.specification)
        }
        return PagedResponse(
            timeEvidenceRepository.findAll(spec, criteria.paging!!).map { TimeEvidenceResponse.fromEntity(it) })
    }
    fun getTimeEvidence(auth: Authentication, criteria: EntityCriteria<TimeEvidenceEntity>): PagedResponse<TimeEvidenceResponse> {
        var spec = Specification<TimeEvidenceEntity> {root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<ProjectEntity>("project").get<PersonEntity>("projectManager").get<Int>("id"), (auth.principal as UserPrincipal).getPersonId())
        }
        if (criteria.specification != null) {
            spec = spec.and(criteria.specification)
        }
        return PagedResponse(
            timeEvidenceRepository.findAll(spec, criteria.paging!!)
                .map { TimeEvidenceResponse.fromEntity(it) })
    }
    fun deleteTimeEvidence(auth: Authentication ,id: Int) {
        if (timeEvidenceRepository.findById(id).orElseThrow { throw NotFoundException("Time evidence", "id", id) }
                .let {
                    if (it.person?.id != (auth.principal as UserPrincipal).getPersonId()) {
                        throw ForbiddenException("Cannot delete someone else's time evidence")
                    }
                    it.status == "ACCEPTED" }) {
            throw BadRequestException("Cannot delete accepted time evidence")
        }
        timeEvidenceRepository.deleteById(id)
    }

    fun fromDto(request: TimeEvidenceRequest, project: ProjectEntity, auth: Authentication) = TimeEvidenceEntity(
        date = request.date,
        minutes = request.minutes,
        comment = request.comment,
        person = (auth.principal as UserPrincipal).getPersonId().let {
            if (it == null) {
                throw BadRequestException("User does not have a Person assigned to them")
            }
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        },
        project = project,
        status = Status.SENT.name
    )

    fun fromDto(request: TimeEvidenceRequest, entity: TimeEvidenceEntity, auth: Authentication): TimeEvidenceEntity {
        val project = request.project.let {
            projectRepository.findById(it).orElseThrow {
                NotFoundException("Project", "id", it)
            }
        }
        if (project.projectMembers.firstOrNull { it.id == (auth.principal as UserPrincipal).getPersonId() } == null) {
            throw BadRequestException("Person is not assigned to the project")
        }
        entity.project = project
        entity.minutes = request.minutes
        entity.comment = request.comment
        entity.status = Status.SENT.name
        entity.statusComment = null
        return entity
    }
}