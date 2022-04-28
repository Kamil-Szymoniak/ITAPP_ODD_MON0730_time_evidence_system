package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.ProjectRequest
import pl.edu.pwr.timeevidence.entity.ProjectEntity
import pl.edu.pwr.timeevidence.service.ProjectService
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import pl.edu.pwr.timeevidence.specification.QueryRequest
import pl.edu.pwr.timeevidence.specification.entity.ProjectSpecification
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/projects")
class ProjectController(private val projectService: ProjectService) {
    @PostMapping
    @PreAuthorize("hasAuthority('CAN_EDIT_PROJECTS')")
    fun createProject(@Valid @RequestBody request: ProjectRequest) = ResponseEntity
        .created(UriBuilder.getUri("/projects/{id}", projectService.createProject(request)))
        .body(BasicResponse("Project added successfully", true))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_PROJECTS')")
    fun editProject(@Valid @RequestBody request: ProjectRequest, @PathVariable id: Int) = ResponseEntity
        .ok(BasicResponse("Project edited successfully", true, projectService.editProject(request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PROJECTS', 'CAN_EDIT_PROJECTS')")
    fun getProjects(@PathVariable id: Int) = ResponseEntity.ok(projectService.getProject(id))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PROJECTS', 'CAN_EDIT_PROJECTS')")
    @QueryRequest(specification = ProjectSpecification::class)
    fun getProjects(criteria: EntityCriteria<ProjectEntity>) = ResponseEntity.ok(projectService.getProjects(criteria))
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_PROJECTS', 'CAN_EDIT_PROJECTS')")
    fun getAllProjects() = ResponseEntity.ok(projectService.getAllProjects())
    @GetMapping("/me")
    @PreAuthorize("permitAll()")
    fun getAllPersonProjects(auth: Authentication) = ResponseEntity.ok(projectService.getMyProjects(auth))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_PROJECTS')")
    fun deleteProject(@PathVariable id: Int): ResponseEntity<BasicResponse> {
        projectService.deleteProject(id)
        return ResponseEntity.ok(BasicResponse("Project deleted successfully", true))
    }
}
