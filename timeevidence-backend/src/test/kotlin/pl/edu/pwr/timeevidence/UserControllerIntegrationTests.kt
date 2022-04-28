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
import pl.edu.pwr.timeevidence.dto.ChangePasswordRequest
import pl.edu.pwr.timeevidence.dto.LoginRequest
import pl.edu.pwr.timeevidence.dto.UserRequest

@SpringBootTest(classes = [TimeEvidenceApplication::class])
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@SqlGroup(Sql(scripts = ["/scripts/create-test-user.sql"]))
class UserControllerIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun registerUser_withoutPermissions_then403() {
        val request = UserRequest("new username", "email@email.email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun registerUsers_invalidRequest_then400() {
        val request = UserRequest("", "email@email.email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("username"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun registerUser_then201() {
        val request = UserRequest("new username", "email@email.email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(201))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login_invalidRequest_then400() {
        val request = LoginRequest("", "testtest")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("login"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login_wrongCredentials_then401() {
        val request = LoginRequest("asdawdwa", "testtest")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Wrong login or password"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login_then200() {
        val request = LoginRequest("NAME1", "testtest")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun logout_then200() {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout"))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Logged out successfully"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editPassword_withoutAccount_then401() {
        val request = ChangePasswordRequest("", "!QAZxsw2")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/me/password")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }


    @Test
    @WithMockCustomUser(username = "NAME1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun editPassword_invalidRequest_then400() {
        val request = ChangePasswordRequest("", "!QAZxsw2")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/user/me/password")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("oldPassword"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockCustomUser(username = "NAME1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun editPassword_then200() {
        val request = ChangePasswordRequest("testtest", "!QAZxsw2")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/user/me/password")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Password changed"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editUser_withoutAccount_then401() {
        val request = UserRequest("new username", "new email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun editUser_withoutPermissions_then403() {
        val request = UserRequest("new username", "email@email.email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun editUser_nonExistent_then404() {
        val request = UserRequest("new username", "email@email.email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun editUsers_invalidRequest_then400() {
        val request = UserRequest("", "email@email.email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("username"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun editUser_then200() {
        val request = UserRequest("new username", "email@email.email", "12345678", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.username").value("new username"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.email").value("email@email.email"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[2].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[2].description").isEmpty)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("new username"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@email.email"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[2].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[2].description").isEmpty)

    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun editUser_lessRoles_then200() {
        val request = UserRequest("new username", "email@email.email", "12345678", listOf(1))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.username").value("new username"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.email").value("email@email.email"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.roles[0].description").isEmpty)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("new username"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@email.email"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].description").isEmpty)

    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getUser_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getUser_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_USERS"])
    @Throws(java.lang.Exception::class)
    fun getUser_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_USERS"])
    @Throws(java.lang.Exception::class)
    fun getUser_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("EMAIL1@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].description").isEmpty)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getMe_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockCustomUser(username = "NAME1", personId = 1)
    @Throws(java.lang.Exception::class)
    fun getMe_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("EMAIL1@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[1].description").isEmpty)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getUsers_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getUsers_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_USERS"])
    @Throws(java.lang.Exception::class)
    fun getUsers_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_USERS"])
    @Throws(java.lang.Exception::class)
    fun getUsers_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users?search=id>0&sortBy=id&sortOrder=ASC")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].username").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].username").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].username").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].email").value("EMAIL1@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].email").value("EMAIL2@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].email").value("EMAIL3@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].person").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].person").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].person.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].roles", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].roles[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].roles[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].roles[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].roles[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].roles[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].roles[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].roles", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].roles[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].roles[0].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].roles[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].roles", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].roles[0].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].roles[0].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].roles[0].description").isEmpty)
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_USERS"])
    @Throws(java.lang.Exception::class)
    fun getUsers_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users?search=id>2&sortBy=id&sortOrder=ASC")
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
    fun getAllUsers_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getAllUsers_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_USERS"])
    @Throws(java.lang.Exception::class)
    fun getAllUsers_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/all")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("EMAIL1@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("EMAIL2@E.mail"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].description").value("EMAIL3@E.mail"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun deleteUser_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun deleteUser_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun deleteUsers_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/users/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_USERS"])
    @Throws(java.lang.Exception::class)
    fun deleteUser_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User deleted successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }
}