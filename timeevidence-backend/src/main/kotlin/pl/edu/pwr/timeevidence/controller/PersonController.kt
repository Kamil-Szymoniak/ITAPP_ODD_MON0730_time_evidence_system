package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.PersonRequest
import pl.edu.pwr.timeevidence.entity.PersonEntity
import pl.edu.pwr.timeevidence.service.PersonService
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import pl.edu.pwr.timeevidence.specification.QueryRequest
import pl.edu.pwr.timeevidence.specification.entity.PersonSpecification
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/persons")
class PersonController(private val personService: PersonService) {
    @PostMapping
    @PreAuthorize("hasAuthority('CAN_EDIT_PERSONS')")
    fun createPerson(@Valid @RequestBody request: PersonRequest) = ResponseEntity
        .created(UriBuilder.getUri("/persons/{id}", personService.createPerson(request)))
        .body(BasicResponse("Person added successfully", true))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_PERSONS')")
    fun editPerson(@Valid @RequestBody request: PersonRequest, @PathVariable id: Int) = ResponseEntity
        .ok(BasicResponse("Person edited successfully", true, personService.editPerson(request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERSONS', 'CAN_EDIT_PERSONS')")
    fun getPerson(@PathVariable id: Int) = ResponseEntity.ok(personService.getPerson(id))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERSONS', 'CAN_EDIT_PERSONS')")
    @QueryRequest(specification = PersonSpecification::class)
    fun getPersons(criteria: EntityCriteria<PersonEntity>) = ResponseEntity.ok(personService.getPersons(criteria))
    @GetMapping("/projects/{projectId}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERSONS', 'CAN_EDIT_PERSONS') and hasAnyAuthority('CAN_SEE_PROJECTS', 'CAN_EDIT_PROJECTS')")
    @QueryRequest(specification = PersonSpecification::class)
    fun getProjectMembers(@PathVariable projectId: Int, criteria: EntityCriteria<PersonEntity>) =
        ResponseEntity.ok(personService.getPersonsInProject(projectId, criteria))
    @GetMapping("/teams/{teamId}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERSONS', 'CAN_EDIT_PERSONS') and hasAnyAuthority('CAN_SEE_TEAMS', 'CAN_EDIT_TEAMS')")
    @QueryRequest(specification = PersonSpecification::class)
    fun getTeamMembers(@PathVariable teamId: Int, criteria: EntityCriteria<PersonEntity>) =
        ResponseEntity.ok(personService.getPersonsInTeam(teamId, criteria))
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PERSONS', 'CAN_EDIT_PERSONS')")
    fun getAllPersons() = ResponseEntity.ok(personService.getAllPersons())
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_PERSONS')")
    fun deletePerson(@PathVariable id: Int): ResponseEntity<BasicResponse> {
        personService.deletePerson(id)
        return ResponseEntity.ok(BasicResponse("Person deleted successfully", true))
    }
}
