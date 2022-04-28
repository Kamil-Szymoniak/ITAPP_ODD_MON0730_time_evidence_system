package pl.edu.pwr.timeevidence.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.edu.pwr.timeevidence.UriBuilder
import pl.edu.pwr.timeevidence.dto.AvailabilityRequest
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.entity.AvailabilityEntity
import pl.edu.pwr.timeevidence.service.AvailabilityService
import pl.edu.pwr.timeevidence.specification.EntityCriteria
import pl.edu.pwr.timeevidence.specification.QueryRequest
import pl.edu.pwr.timeevidence.specification.entity.AvailabilitySpecification
import javax.validation.Valid

@RestController
@Validated
@RequestMapping("/availability")
class AvailabilityController(private val availabilityService: AvailabilityService) {
    @PostMapping
    fun createAvailability(@Valid @RequestBody request: AvailabilityRequest, auth: Authentication) = ResponseEntity
        .created(UriBuilder.getUri("/time-evidence/{id}", availabilityService.createAvailability(auth, request)))
        .body(BasicResponse("Availability added successfully", true))
    @PutMapping("/{id}")
    @PreAuthorize("permitAll()")
    fun editAvailability(@Valid @RequestBody request: AvailabilityRequest, @PathVariable id: Int, auth: Authentication) = ResponseEntity
        .ok(BasicResponse("Availability edited successfully", true, availabilityService.editAvailability(auth, request, id)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CAN_SEE_AVAILABILITY', 'CAN_EDIT_AVAILABILITY')")
    fun getAvailability(@PathVariable id: Int) = ResponseEntity.ok(availabilityService.getAvailability(id))
    @GetMapping("/month/{monthIndex}")
    fun getAvailabilityInAMonth(auth: Authentication, @PathVariable monthIndex: Int) =
        ResponseEntity.ok(availabilityService.getAvailabilityInAMonth(auth, monthIndex))
    @GetMapping("/user/{date}")
    fun getUserAvailability(auth: Authentication, @PathVariable date: String) =
        ResponseEntity.ok(availabilityService.getUserAvailability(auth, date))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CAN_SEE_AVAILABILITY', 'CAN_EDIT_AVAILABILITY')")
    @QueryRequest(specification = AvailabilitySpecification::class)
    fun getAvailability(auth: Authentication, criteria: EntityCriteria<AvailabilityEntity>) =
        ResponseEntity.ok(availabilityService.getAvailability(auth, criteria))
    @DeleteMapping("/{id}")
    fun deleteAvailability(@PathVariable id: Int, auth: Authentication): ResponseEntity<BasicResponse> {
        availabilityService.deleteAvailability(id, auth)
        return ResponseEntity.ok(BasicResponse("Availability deleted successfully", true))
    }
}
