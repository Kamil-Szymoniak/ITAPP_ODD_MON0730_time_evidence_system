package pl.edu.pwr.timeevidence.service

import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import pl.edu.pwr.timeevidence.LocalDateParser
import pl.edu.pwr.timeevidence.config.UserPrincipal
import pl.edu.pwr.timeevidence.dao.AvailabilityRepository
import pl.edu.pwr.timeevidence.dao.PersonRepository
import pl.edu.pwr.timeevidence.dao.TeamRepository
import pl.edu.pwr.timeevidence.dto.*
import pl.edu.pwr.timeevidence.entity.*
import pl.edu.pwr.timeevidence.exception.BadRequestException
import pl.edu.pwr.timeevidence.exception.ForbiddenException
import pl.edu.pwr.timeevidence.exception.NotFoundException
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import java.time.LocalDate
import java.time.YearMonth

@Service
class AvailabilityService(
    private val availabilityRepository: AvailabilityRepository,
    private val personRepository: PersonRepository,
    private val teamRepository: TeamRepository
) {
    fun createAvailability(auth: Authentication, request: AvailabilityRequest): Int {
        val team = request.team.let {
            teamRepository.findById(it).orElseThrow {
                NotFoundException("Team", "id", it)
            }
        }
        if (team.teamMembers.firstOrNull { it.id == (auth.principal as UserPrincipal).getPersonId() } == null) {
            throw BadRequestException("Person is not assigned to the team")
        }
        val spec = Specification<AvailabilityEntity> { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<PersonEntity>("person").get<Int>("id"), (auth.principal as UserPrincipal).getPersonId())
        }.and { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<LocalDate>("date"), request.date)
        }
        if (availabilityRepository.findAll(spec).firstOrNull { it.team?.id == request.team } != null) {
            throw BadRequestException("Availability for this team at this date already exists. Edit existing availability instead.")
        }
        return availabilityRepository.save(fromDto(request, auth, team)).id!!
    }
    fun editAvailability(auth: Authentication, request: AvailabilityRequest, id: Int): AvailabilityResponse =
        availabilityRepository.findById(id).orElseThrow {
            NotFoundException("Availability", "id", id)
        }.let {
            if ((auth.principal as UserPrincipal).getPersonId() == it.person!!.id) {
                AvailabilityResponse.fromEntity(availabilityRepository.save(fromDto(request, it, auth)))
            } else {
                throw ForbiddenException("User cannot edit someone else's availability")
            }
        }
    fun getAvailability(id: Int) = AvailabilityResponse.fromEntity(availabilityRepository.findById(id)
        .orElseThrow { throw NotFoundException("Availability", "id", id) })
    fun getAvailabilityInAMonth(auth: Authentication, monthIndex: Int): HashMap<Int, Int> {
        if (monthIndex < 1 || monthIndex > 12) {
            throw BadRequestException("Month index should be between 1 and 12")
        }
        val currentYear = LocalDate.now().year
        val numberOfDays = YearMonth.of(currentYear, monthIndex).lengthOfMonth()
        val localDateMin = LocalDate.of(currentYear, monthIndex, 1)
        val localDateMax = LocalDate.of(currentYear, monthIndex, numberOfDays)
        val spec = Specification<AvailabilityEntity> { root, _, criteriaBuilder ->
            criteriaBuilder.between(root.get("date"), localDateMin, localDateMax)
        }.and {root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<PersonEntity>("person").get<Int>("id"), (auth.principal as UserPrincipal).getPersonId())
        }
        val map = HashMap<Int, Int>()
        for (i in 1..numberOfDays) {
            map[i] = 0
        }
        availabilityRepository.findAll(spec).map { AvailabilityResponse.fromEntity(it) }.forEach {
            map[it.date.dayOfMonth] = it.periods.sumOf { period -> period.minutes }
        }
        return map
    }
    fun getUserAvailability(auth: Authentication, stringDate: String): List<AvailabilityResponse> {
        val spec = Specification<AvailabilityEntity> { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<PersonEntity>("person").get<Int>("id"), (auth.principal as UserPrincipal).getPersonId())
        }.and { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<LocalDate>("date"), LocalDateParser.parseLocalDate(stringDate))
        }
        return availabilityRepository.findAll(spec).map { AvailabilityResponse.fromEntity(it) }
    }
    fun getAvailability(auth: Authentication, criteria: EntityCriteria<AvailabilityEntity>): PagedResponse<AvailabilityResponse> {
        var spec = Specification<AvailabilityEntity> {root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<TeamEntity>("team").get<PersonEntity>("teamLeader").get<Int>("id"), (auth.principal as UserPrincipal).getPersonId())
        }
        if (criteria.specification != null) {
            spec = spec.and(criteria.specification)
        }
        return PagedResponse(
            availabilityRepository.findAll(spec, criteria.paging!!)
                .map { AvailabilityResponse.fromEntity(it) })
    }
    fun deleteAvailability(id: Int, auth: Authentication) {
        availabilityRepository.findById(id).orElseThrow { throw NotFoundException("Availability", "id", id) }.let {
            if (it.person?.id != (auth.principal as UserPrincipal).getPersonId()) {
                throw ForbiddenException("Cannot delete someone else's availability")
            }
        }
        availabilityRepository.deleteById(id)
    }

    fun fromDto(request: AvailabilityRequest, auth: Authentication, team: TeamEntity) = AvailabilityEntity(
        comment = request.comment,
        date = request.date,
        person = (auth.principal as UserPrincipal).getPersonId().let {
            if (it == null) {
                throw BadRequestException("No person is assigned to your account")
            }
            personRepository.findById(it).orElseThrow {
                NotFoundException("Person", "id", it)
            }
        },
        team = team,
        periods = PeriodRequest.toString(request.periods)
    )

    fun fromDto(request: AvailabilityRequest, entity: AvailabilityEntity, auth: Authentication): AvailabilityEntity {
        val team = request.team.let {
            teamRepository.findById(it).orElseThrow {
                NotFoundException("Team", "id", it)
            }
        }
        if (team.teamMembers.firstOrNull { it.id == (auth.principal as UserPrincipal).getPersonId() } == null) {
            throw BadRequestException("Person is not assigned to the team")
        }
        entity.team = team
        entity.periods = PeriodRequest.toString(request.periods)
        entity.comment = request.comment
        return entity
    }
}