package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.TeamRequest
import pl.edu.pwr.timeevidence.entity.TeamEntity
import pl.edu.pwr.timeevidence.service.TeamService
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import pl.edu.pwr.timeevidence.specification.QueryRequest
import pl.edu.pwr.timeevidence.specification.entity.TeamSpecification
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/teams")
class TeamController(private val teamService: TeamService) {
    @PostMapping
    @PreAuthorize("hasAuthority('CAN_EDIT_TEAMS')")
    fun createTeam(@Valid @RequestBody request: TeamRequest) = ResponseEntity
        .created(UriBuilder.getUri("/teams/{id}", teamService.createTeam(request)))
        .body(BasicResponse("Team added successfully", true))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_TEAMS')")
    fun editTeam(@Valid @RequestBody request: TeamRequest, @PathVariable id: Int) = ResponseEntity
        .ok(BasicResponse("Team edited successfully", true, teamService.editTeam(request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_TEAMS', 'CAN_EDIT_TEAMS')")
    fun getTeam(@PathVariable id: Int) = ResponseEntity.ok(teamService.getTeam(id))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_TEAMS', 'CAN_EDIT_TEAMS')")
    @QueryRequest(specification = TeamSpecification::class)
    fun getTeams(criteria: EntityCriteria<TeamEntity>) = ResponseEntity.ok(teamService.getTeams(criteria))
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_TEAMS', 'CAN_EDIT_TEAMS')")
    fun getAllTeams() = ResponseEntity.ok(teamService.getAllTeams())
    @GetMapping("/me")
    @PreAuthorize("permitAll()")
    fun getAllPersonTeams(auth: Authentication) = ResponseEntity.ok(teamService.getMyTeams(auth))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_TEAMS')")
    fun deleteTeam(@PathVariable id: Int): ResponseEntity<BasicResponse> {
        teamService.deleteTeam(id)
        return ResponseEntity.ok(BasicResponse("Team deleted successfully", true))
    }
}
