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
import pl.edu.pwr.timeevidence.dto.ProjectRequest
import java.time.LocalDate

@SpringBootTest(classes = [TimeEvidenceApplication::class])
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@SqlGroup(Sql(scripts = ["/scripts/create-test-project.sql"]))
class ProjectControllerIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Throws(Exception::class)
    fun createProject_withoutAccount_then401() {
        val request = ProjectRequest("name", "inhouse name","description", "client name", LocalDate.of(2022, 12, 12), listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun createProject_withoutPermissions_then403() {
        val request = ProjectRequest("name", "inhouse name","description", "client name", LocalDate.of(2022, 12, 12), listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun createProject_invalidRequest_then400() {
        val request = ProjectRequest("", "inhouse name","description", "client name", LocalDate.of(2022, 12, 12), listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun createProject_then201() {
        val request = ProjectRequest("name", "inhouse name","description", "client name", LocalDate.of(2022, 12, 12), listOf(1, 2), 1)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(201))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project added successfully"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editProject_withoutAccount_then401() {
        val request = ProjectRequest("new name", "new inhouse name","new description", "new client name", LocalDate.of(2022, 12, 12), listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun editProject_withoutPermissions_then403() {
        val request = ProjectRequest("new name", "new inhouse name","new description", "new client name", LocalDate.of(2022, 12, 12), listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun editProject_nonExistent_then404() {
        val request = ProjectRequest("new name", "new inhouse name","new description", "new client name", LocalDate.of(2022, 12, 12), listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun editProjects_invalidRequest_then400() {
        val request = ProjectRequest("", "new inhouse name","new description", "new client name", LocalDate.of(2022, 12, 12), listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun editProject_then200() {
        val request = ProjectRequest("new name", "new inhouse name","new description", "new client name", LocalDate.of(2022, 12, 12), listOf(1, 2, 3), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectManager.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectManager.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[1].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[2].name").value("NAME3 SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[2].description").value("0048666420656"))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectManager.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectManager.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[1].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[2].name").value("NAME3 SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[2].description").value("0048666420656"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun editProject_lessMembers_then200() {
        val request = ProjectRequest("new name", "new inhouse name","new description", "new client name", LocalDate.of(2022, 12, 12), listOf(2), 2)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectManager.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectManager.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[0].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.projectMembers[0].description").value("666420656"))


        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectManager.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectManager.name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].description").value("666420656"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getProject_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getProject_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getProject_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getProject_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("DESCRIPTION1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectManager.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.projectMembers[1].description").value("666420656"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getProjects_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getProjects_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getProjects_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getProjects_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects?search=id>0&sortBy=id&sortOrder=ASC")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectManager.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].projectManager.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].projectManager.id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectMembers", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectMembers[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectMembers[0].name").value("NAME1 SURNAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectMembers[0].description").value("+48666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectMembers[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectMembers[1].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].projectMembers[1].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].projectMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].projectMembers[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].projectMembers[0].name").value("NAME2 SURNAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].projectMembers[0].description").value("666420656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].projectMembers", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].projectMembers[0].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].projectMembers[0].name").value("NAME3 SURNAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].projectMembers[0].description").value("0048666420656"))

    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getProjects_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects?search=id>2&sortBy=id&sortOrder=ASC")
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
    fun getAllProjects_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getAllProjects_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun getAllProjects_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/all")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Client name: CLIENT NAME2,\nProject manager: NAME2 SURNAME2,\nNumber of project members: 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].description").value("Client name: CLIENT NAME3,\nProject manager: NAME3 SURNAME3,\nNumber of project members: 1"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getMyProjects_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "NAME1", personId = 2)
    @Throws(java.lang.Exception::class)
    fun getMyProjects_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Client name: CLIENT NAME1,\nProject manager: NAME1 SURNAME1,\nNumber of project members: 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Client name: CLIENT NAME2,\nProject manager: NAME2 SURNAME2,\nNumber of project members: 1"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun deleteProject_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun deleteProject_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun deleteProject_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_PROJECTS"])
    @Throws(java.lang.Exception::class)
    fun deleteProject_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Project deleted successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }
}