package pl.edu.pwr.timeevidence

import WithMockCustomUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import pl.edu.pwr.timeevidence.dto.AvailabilityRequest
import pl.edu.pwr.timeevidence.dto.PeriodRequest
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest(classes = [TimeEvidenceApplication::class])
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@SqlGroup(Sql(scripts = ["/scripts/create-test-availability.sql"]))
class AvailabilityControllerIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val periods = listOf(PeriodRequest(LocalTime.of(12, 0), LocalTime.of(13, 0)), PeriodRequest(LocalTime.of(13, 0), LocalTime.of(14, 0)))
    private val newPeriods = listOf(PeriodRequest(LocalTime.of(12, 0), LocalTime.of(13, 0)), PeriodRequest(LocalTime.of(13, 0), LocalTime.of(14, 0)), PeriodRequest(LocalTime.of(15, 0), LocalTime.of(16, 0)))
    private val wrongPeriods = listOf(PeriodRequest(LocalTime.of(12, 0), LocalTime.of(13, 0)), PeriodRequest(LocalTime.of(12, 0), LocalTime.of(14, 0)))


    @Test
    @Throws(Exception::class)
    fun createAvailability_withoutAccount_then401() {
        val request = AvailabilityRequest("comment", LocalDate.now(), 1, periods)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/availability")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun createAvailability_invalidRequest_then400() {
        val request = AvailabilityRequest("comment", LocalDate.now(), 1, wrongPeriods)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/availability")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Period should not start or end during another one"))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun createAvailability_notATeamMember_then400() {
        val request = AvailabilityRequest("comment", LocalDate.now(), 2, periods)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/availability")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person is not assigned to the team"))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun createAvailability_theSameTeam_then400() {
        val request = AvailabilityRequest("comment", LocalDate.now(), 1, periods)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/availability")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/availability")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Availability for this team at this date already exists. Edit existing availability instead."))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun createAvailability_then201() {
        val request = AvailabilityRequest("comment", LocalDate.now(), 1, periods)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/availability")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(201))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Availability added successfully"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editAvailability_withoutAccount_then401() {
        val request = AvailabilityRequest("new comment", LocalDate.now(), 2, newPeriods)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/availability/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun editAvailability_nonExistent_then404() {
        val request = AvailabilityRequest("new comment", LocalDate.now(), 2, newPeriods)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/availability/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Availability not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun editAvailability_invalidRequest_then400() {
        val request = AvailabilityRequest("new comment", LocalDate.now(), 1, wrongPeriods)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/availability/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Period should not start or end during another one"))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun editAvailability_notInTheTeam_then400() {
        val request = AvailabilityRequest("new comment", LocalDate.of(2040, 1, 1), 2, newPeriods)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/availability/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person is not assigned to the team"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 2)
    @Throws(java.lang.Exception::class)
    fun editAvailability_someoneElse_then403() {
        val request = AvailabilityRequest("new comment", LocalDate.of(2040, 1, 1), 1, newPeriods)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/availability/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User cannot edit someone else's availability"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_AVAILABILITY"])
    @Throws(java.lang.Exception::class)
    fun editAvailability_then200() {

        val request = AvailabilityRequest("new comment", LocalDate.of(2040, 1, 1), 1, newPeriods)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/availability/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.comment").value("new comment"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.date").value("2030-01-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[0].timeFrom").value("12:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[0].timeTo").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[0].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[1].timeFrom").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[1].timeTo").value("14:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[1].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[2].timeFrom").value("15:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[2].timeTo").value("16:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[2].minutes").value(60))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value("new comment"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.date").value("2030-01-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods", Matchers.hasSize<Any>(3)))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_AVAILABILITY"])
    @Throws(java.lang.Exception::class)
    fun editAvailability_lessMembers_then200() {
        val request = AvailabilityRequest("new comment", LocalDate.of(2040, 1, 1), 1, listOf(PeriodRequest(LocalTime.of(3, 0), LocalTime.of(4, 0))))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/availability/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.comment").value("new comment"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.date").value("2030-01-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[0].timeFrom").value("03:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[0].timeTo").value("04:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.periods[0].minutes").value(60))



        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value("new comment"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.date").value("2030-01-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods", Matchers.hasSize<Any>(1)))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getAvailability_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getAvailability_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_AVAILABILITY"])
    @Throws(java.lang.Exception::class)
    fun getAvailability_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Availability not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_AVAILABILITY"])
    @Throws(java.lang.Exception::class)
    fun getAvailability_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value("COMMENT1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.date").value("2030-01-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods[0].timeFrom").value("12:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods[0].timeTo").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods[0].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods[1].timeFrom").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods[1].timeTo").value("14:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.periods[1].minutes").value(60))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getAvailabilities_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getAvailabilities_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_AVAILABILITY"])
    @Throws(java.lang.Exception::class)
    fun getAvailabilities_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_AVAILABILITY"])
    @Throws(java.lang.Exception::class)
    fun getAvailabilities_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability?search=id>0&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].comment").value("COMMENT1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].comment").value("COMMENT2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date").value("2030-01-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].date").value("2030-01-02"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].periods", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].periods[0].timeFrom").value("12:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].periods[0].timeTo").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].periods[0].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].periods[1].timeFrom").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].periods[1].timeTo").value("14:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].periods[1].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].periods", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].periods[0].timeFrom").value("12:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].periods[0].timeTo").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].periods[0].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].periods[1].timeFrom").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].periods[1].timeTo").value("14:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].periods[1].minutes").value(60))


    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_AVAILABILITY"])
    @Throws(java.lang.Exception::class)
    fun getAvailabilities_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability?search=id>2&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(2))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getUserAvailabilities_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/user/2030-01-01")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getUserAvailabilities_invalidDate_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/user/2030a-01-01")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The date string 2030a-01-01 failed to be parsed"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getUserAvailabilities_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/user/2030-01-01")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].comment").value("COMMENT1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].date").value("2030-01-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].team.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].periods", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].periods[0].timeFrom").value("12:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].periods[0].timeTo").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].periods[0].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].periods[1].timeFrom").value("13:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].periods[1].timeTo").value("14:00:00"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].periods[1].minutes").value(60))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getAvailabilityInAMonth_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/month/{monthIndex}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getAvailabilityInAMonth_invalidIndex_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/month/{monthIndex}", 15)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Month index should be between 1 and 12"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getAvailabilityInAMonth_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/availability/month/{monthIndex}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun deleteAvailability_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 2)
    @Throws(java.lang.Exception::class)
    fun deleteAvailability_someoneElses_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Cannot delete someone else's availability"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun deleteAvailability_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/availability/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Availability not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun deleteAvailability_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/availability/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Availability deleted successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }
}