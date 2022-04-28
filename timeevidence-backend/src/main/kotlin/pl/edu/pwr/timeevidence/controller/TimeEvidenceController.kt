package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.TimeEvidenceChangeStatusRequest
import pl.edu.pwr.timeevidence.dto.TimeEvidenceRequest
import pl.edu.pwr.timeevidence.entity.TimeEvidenceEntity
import pl.edu.pwr.timeevidence.service.TimeEvidenceService
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import pl.edu.pwr.timeevidence.specification.QueryRequest
import pl.edu.pwr.timeevidence.specification.entity.TimeEvidenceSpecification
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/time-evidence")
class TimeEvidenceController(private val timeEvidenceService: TimeEvidenceService) {
    @PostMapping
    fun createTimeEvidence(@Valid @RequestBody request: TimeEvidenceRequest, auth: Authentication) = ResponseEntity
        .created(UriBuilder.getUri("/time-evidence/{id}", timeEvidenceService.createTimeEvidence(request, auth)))
        .body(BasicResponse("Time evidence added successfully", true))
    @PutMapping("/{id}")
    fun editTimeEvidence(@Valid @RequestBody request: TimeEvidenceRequest, @PathVariable id: Int, auth: Authentication) = ResponseEntity
        .ok(BasicResponse("Time evidence edited successfully", true, timeEvidenceService.editTimeEvidence(request, id, auth)))
    @PutMapping("/status/{id}")
    @PreAuthorize("hasAuthority('CAN_EDIT_EVIDENCE')")
    fun editTimeEvidenceStatus(@Valid @RequestBody request: TimeEvidenceChangeStatusRequest, @PathVariable id: Int, auth: Authentication): ResponseEntity<BasicResponse> {
        timeEvidenceService.changeTimeEvidenceStatus(request, id, auth)
        return ResponseEntity.ok(BasicResponse("Time evidence status changed to ${request.status}", true))
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_EVIDENCE', 'CAN_EDIT_EVIDENCE')")
    fun getTimeEvidence(@PathVariable id: Int) = ResponseEntity.ok(timeEvidenceService.getTimeEvidence(id))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_EVIDENCE', 'CAN_EDIT_EVIDENCE')")
    @QueryRequest(specification = TimeEvidenceSpecification::class)
    fun getTimeEvidence(auth: Authentication, criteria: EntityCriteria<TimeEvidenceEntity>) =
        ResponseEntity.ok(timeEvidenceService.getTimeEvidence(auth, criteria))
    @GetMapping("/user")
    @PreAuthorize("permitAll()")
    @QueryRequest(specification = TimeEvidenceSpecification::class)
    fun getUserTimeEvidence(auth: Authentication, criteria: EntityCriteria<TimeEvidenceEntity>) =
        ResponseEntity.ok(timeEvidenceService.getUserTimeEvidence(auth, criteria))
    @GetMapping("/month/{monthIndex}")
    fun getTimeEvidenceInAMonth(@PathVariable monthIndex: Int, auth: Authentication) =
        ResponseEntity.ok(timeEvidenceService.getTimeEvidenceInAMonth(auth, monthIndex))
    @DeleteMapping("/{id}")
    fun deleteTimeEvidence(@PathVariable id: Int, auth: Authentication): ResponseEntity<BasicResponse> {
        timeEvidenceService.deleteTimeEvidence(auth, id)
        return ResponseEntity.ok(BasicResponse("Time evidence deleted successfully", true))
    }
}
