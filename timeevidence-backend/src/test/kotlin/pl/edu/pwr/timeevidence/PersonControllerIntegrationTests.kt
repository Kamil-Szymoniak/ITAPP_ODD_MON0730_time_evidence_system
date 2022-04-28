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
import pl.edu.pwr.timeevidence.dto.PersonRequest
import java.time.LocalDate

@SpringBootTest(classes = [TimeEvidenceApplication::class])
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@SqlGroup(Sql(scripts = ["/scripts/create-test-person.sql"]))
class PersonControllerIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Throws(Exception::class)
    fun createPerson_withoutAccount_then401() {
        val request = PersonRequest("name", "surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/persons")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun createPerson_withoutPermissions_then403() {
        val request = PersonRequest("name", "surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/persons")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun createPerson_invalidRequest_then400() {
        val request = PersonRequest("", "surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/persons")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun createPerson_then201() {
        val request = PersonRequest("name", "surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/persons")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(201))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person added successfully"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editPerson_withoutAccount_then401() {
        val request = PersonRequest("new name", "new surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/persons/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun editPerson_withoutPermissions_then403() {
        val request = PersonRequest("new name", "new surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/persons/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun editPerson_nonExistent_then404() {
        val request = PersonRequest("new name", "new surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/persons/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun editPerson_invalidRequest_then400() {
        val request = PersonRequest("", "new surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/persons/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun editPerson_then200() {
        val request = PersonRequest("new name", "new surname", "660317614", LocalDate.of(2001, 12, 12))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/persons/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.surname").value("new surname"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.phone").value("660317614"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.birthday").value("2001-12-12"))


    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getPerson_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getPerson_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun getPerson_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun getPerson_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2001-10-01"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getPersons_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getPersons_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun getPersons_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun getPersons_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons?search=id>0&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].surname").value("SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].phone").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].birthday").value("2001-10-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.description").value("EMAIL1@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].surname").value("SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].phone").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].birthday").value("2001-10-02"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].user").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].surname").value("SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].phone").value("0048666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].birthday").value("2001-10-03"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].user").isEmpty)
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun getPersons_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons?search=id>2&sortBy=id&sortOrder=ASC")
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
    fun getPersonsInTeam_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/teams/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getPersonsInTeam_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/teams/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS", "CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInTeam_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/teams/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Team not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS", "CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInTeam_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/teams/{id}?search=ide>0", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS", "CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInTeam_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/teams/{id}?search=id>0&sortBy=id&sortOrder=ASC", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].surname").value("SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].phone").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].birthday").value("2001-10-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.description").value("EMAIL1@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].surname").value("SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].phone").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].birthday").value("2001-10-02"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].user").isEmpty)
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS", "CAN_SEE_TEAMS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInTeam_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/teams/{id}?search=id>2&sortBy=id&sortOrder=ASC", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(2))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getPersonsInProject_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getPersonsInProject_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS", "CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInProject_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/projects/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockCustomUser(username = "user1", personId = 1, authorities = ["CAN_SEE_PERSONS", "CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInProjects_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/projects/{id}?search=ide>0", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS", "CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInProject_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/projects/{id}?search=id>0&sortBy=id&sortOrder=ASC", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].surname").value("SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].phone").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].birthday").value("2001-10-01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].user.description").value("EMAIL1@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].surname").value("SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].phone").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].birthday").value("2001-10-02"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].user").isEmpty)
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS", "CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getPersonsInProject_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/projects/{id}?search=id>2&sortBy=id&sortOrder=ASC", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(2))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getAllPersons_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getAllPersons_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun getAllPersons_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/persons/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value("NAME3 SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].description").value("0048666420656"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun deletePerson_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun deletePerson_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun deletePerson_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/persons/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PERSONS"])
    @Throws(java.lang.Exception::class)
    fun deleteRole_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/persons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Person deleted successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }
}