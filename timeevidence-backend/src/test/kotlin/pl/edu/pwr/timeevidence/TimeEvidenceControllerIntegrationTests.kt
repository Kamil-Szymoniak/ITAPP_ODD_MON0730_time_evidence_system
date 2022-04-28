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
import pl.edu.pwr.timeevidence.dto.TimeEvidenceChangeStatusRequest
import pl.edu.pwr.timeevidence.dto.TimeEvidenceRequest
import java.time.LocalDate

@SpringBootTest(classes = [TimeEvidenceApplication::class])
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@SqlGroup(Sql(scripts = ["/scripts/create-test-time-evidence.sql"]))
class TimeEvidenceControllerIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Throws(Exception::class)
    fun createTimeEvidence_withoutAccount_then401() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 10, "comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/time-evidence")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun createTimeEvidence_invalidRequest_then400() {
        val request = TimeEvidenceRequest(LocalDate.of(2060, 1, 1), 10, "comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/time-evidence")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("date"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must be a date in the past or in the present"))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun createTimeEvidence_notAProjectMember_then400() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 10, "comment", 2)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/time-evidence")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person is not assigned to the project"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun createTimeEvidence_then201() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 10, "comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/time-evidence")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(201))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence added successfully"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editTimeEvidence_withoutAccount_then401() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 20, "new comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidence_nonExistent_then404() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 20, "new comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidences_invalidRequest_then400() {
        val request = TimeEvidenceRequest(LocalDate.of(2060, 1, 1), 20, "new comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("date"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must be a date in the past or in the present"))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidence_notAProjectMember_then400() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 20, "new comment", 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person is not assigned to the project"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").isEmpty)
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidence_someoneElse_then403() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 20, "new comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/{id}", 3)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User cannot edit someone else's time evidence"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").isEmpty)
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidence_statusAccepted_then400() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 20, "new comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/{id}", 4)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Cannot edit accepted request"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").isEmpty)
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidence_then200() {
        val request = TimeEvidenceRequest(LocalDate.of(2022, 1, 1), 20, "new comment", 1)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.date").value("2021-12-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.minutes").value(20))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.comment").value("new comment"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.person.name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.person.description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.project.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.project.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.project.description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))

    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editTimeEvidenceStatus_withoutAccount_then401() {
        val request = TimeEvidenceChangeStatusRequest("ACCEPTED", "Some new comment")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/status/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun editTimeEvidenceStatus_withoutPermissions_then403() {
        val request = TimeEvidenceChangeStatusRequest("ACCEPTED", "Some new comment")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/status/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidenceStatus_nonExistent_then404() {
        val request = TimeEvidenceChangeStatusRequest("ACCEPTED", "Some new comment")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/status/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidencesStatus_invalidRequest_then400() {
        val request = TimeEvidenceChangeStatusRequest("DELETED", "Some new comment")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/status/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("status"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("The field must be either 'ACCEPTED' or 'REJECTED'"))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidenceStatus_notAProjectManager_then403() {
        val request = TimeEvidenceChangeStatusRequest("ACCEPTED", "Some new comment")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/status/{id}", 2)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User is not the pm of this project"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").isEmpty)
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidenceStatus_statusNotSent_then400() {
        val request = TimeEvidenceChangeStatusRequest("ACCEPTED", "Some new comment")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/status/{id}", 4)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("You can only edit time evidence with status: Sent"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").isEmpty)
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun editTimeEvidenceStatus_then200() {
        val request = TimeEvidenceChangeStatusRequest("ACCEPTED", "Some new comment")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/time-evidence/status/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence status changed to ACCEPTED"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getTimeEvidence_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getTimeEvidence_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun getTimeEvidence_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun getTimeEvidence_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.date").value("2021-12-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value("COMMENT1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.person.name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.person.description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.project.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.project.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.project.description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getTimeEvidences_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getTimeEvidences_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun getTimeEvidences_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun getTimeEvidences_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence?search=id>0&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date").value("2021-12-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].date").value("2021-12-04"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].minutes").value(240))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].comment").value("COMMENT1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].comment").value("COMMENT4"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person.name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person.description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person.name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person.description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))

    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_EDIT_EVIDENCE"])
    @Throws(java.lang.Exception::class)
    fun getTimeEvidences_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence?search=id>2&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(4))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getUserTimeEvidences_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/user")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getUserTimeEvidences_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/user?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getUserTimeEvidences_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/user?search=id>0&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date").value("2021-12-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].date").value("2021-12-02"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].date").value("2021-12-04"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].minutes").value(60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].minutes").value(120))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].minutes").value(240))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].comment").value("COMMENT1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].comment").value("COMMENT2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].comment").value("COMMENT4"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person.name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person.description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].project.description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person.name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person.description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].project.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].project.name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].project.description").value("Client name: CLIENT NAME2,\nProject manager: NAME2 SURNAME2,\nNumber of project members: 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].person.name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].person.description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].project.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].project.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].project.description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))

    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getTimeEvidenceInAMonth_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/month/{monthIndex}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getTimeEvidenceInAMonth_invalidIndex_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/month/{monthIndex}", 15)
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
    fun getTimeEvidenceInAMonth_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/time-evidence/month/{monthIndex}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
    }


    @Test
    @Throws(java.lang.Exception::class)
    fun deleteTimeEvidence_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/time-evidence/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun deleteTimeEvidence_statusAccepted_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/time-evidence/{id}", 4)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Cannot delete accepted time evidence"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun deleteTimeEvidence_someoneElses_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/time-evidence/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Cannot delete someone else's time evidence"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun deleteTimeEvidence_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/time-evidence/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun deleteTimeEvidence_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/time-evidence/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Time evidence deleted successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }
}