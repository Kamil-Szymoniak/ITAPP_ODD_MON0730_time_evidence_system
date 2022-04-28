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
import pl.edu.pwr.timeevidence.dto.TeamRequest

@SpringBootTest(classes = [TimeEvidenceApplication::class])
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@SqlGroup(Sql(scripts = ["/scripts/create-test-team.sql"]))
class TeamControllerIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Throws(Exception::class)
    fun createTeam_withoutAccount_then401() {
        val request = TeamRequest("name", "description", listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teams")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun createTeam_withoutPermissions_then403() {
        val request = TeamRequest("name", "description", listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teams")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun createTeam_invalidRequest_then400() {
        val request = TeamRequest("", "description", listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teams")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun createTeam_then201() {
        val request = TeamRequest("name", "description", listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teams")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(201))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team added successfully"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editTeam_withoutAccount_then401() {
        val request = TeamRequest("new name", "new description", listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teams/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun editTeam_withoutPermissions_then403() {
        val request = TeamRequest("new name", "new description", listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teams/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun editTeam_nonExistent_then404() {
        val request = TeamRequest("new name", "new description", listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teams/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun editTeams_invalidRequest_then400() {
        val request = TeamRequest("", "new description", listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teams/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun editTeam_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("DESCRIPTION1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamLeader.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].description").value("666420656"))

        val request = TeamRequest("new name", "new description", listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teams/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamLeader.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamLeader.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[1].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[2].name").value("NAME3 SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[2].description").value("0048666420656"))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamLeader.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamLeader.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[2].name").value("NAME3 SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[2].description").value("0048666420656"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun editTeam_lessMembers_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("DESCRIPTION1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamLeader.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].description").value("666420656"))

        val request = TeamRequest("new name", "new description", listOf(2), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teams/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamLeader.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamLeader.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[0].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.teamMembers[0].description").value("666420656"))


        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamLeader.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamLeader.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].description").value("666420656"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getTeam_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getTeam_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getTeam_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getTeam_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("DESCRIPTION1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamLeader.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.teamMembers[1].description").value("666420656"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getTeams_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getTeams_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getTeams_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getTeams_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams?search=id>0&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description").value("DESCRIPTION1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].description").value("DESCRIPTION2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].description").value("DESCRIPTION3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamLeader.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].teamLeader.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].teamLeader.id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamMembers", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].teamMembers[1].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].teamMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].teamMembers[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].teamMembers[0].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].teamMembers[0].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].teamMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].teamMembers[0].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].teamMembers[0].name").value("NAME3 SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].teamMembers[0].description").value("0048666420656"))

    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getTeams_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams?search=id>2&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(3))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getAllTeams_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getAllTeams_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getAllTeams_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Team leader: NAME1 SURNAME1,\nNumber of team members: 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Team leader: NAME2 SURNAME2,\nNumber of team members: 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].description").value("Team leader: NAME3 SURNAME3,\nNumber of team members: 1"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getMyTeams_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }


    @Test
    @WithMockCustomUser(username = "NAME1", personId = 2)
    @Throws(java.lang.Exception::class)
    fun getMyTeams_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teams/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Team leader: NAME1 SURNAME1,\nNumber of team members: 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Team leader: NAME2 SURNAME2,\nNumber of team members: 1"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun deleteTeam_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun deleteTeam_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun deleteTeam_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teams/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun deleteTeam_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team deleted successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }
}