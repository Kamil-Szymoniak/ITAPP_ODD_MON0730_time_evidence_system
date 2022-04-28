package pl.edu.pwr.timeevidence

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
import pl.edu.pwr.timeevidence.dto.RoleRequest

@SpringBootTest(classes = [TimeEvidenceApplication::class])
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
@SqlGroup(Sql(scripts = ["/scripts/create-test-role.sql"]))
class RoleControllerIntegrationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Throws(Exception::class)
    fun createRole_withoutAccount_then401() {
        val request = RoleRequest("name", "description", listOf(1, 2))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/roles")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun createRole_withoutPermissions_then403() {
        val request = RoleRequest("name", "description", listOf(1, 2))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/roles")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun createRole_invalidRequest_then400() {
        val request = RoleRequest("", "description", listOf(1, 2))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/roles")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun createRole_then201() {
        val request = RoleRequest("name", "description", listOf(1, 2))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/roles")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(201))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role added successfully"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun editRole_withoutAccount_then401() {
        val request = RoleRequest("new name", "new description", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun editRole_withoutPermissions_then403() {
        val request = RoleRequest("new name", "new description", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun editRole_nonExistent_then404() {
        val request = RoleRequest("new name", "new description", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/roles/{id}", 69)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun editRoles_invalidRequest_then400() {
        val request = RoleRequest("", "new description", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].field").value("name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message").value("must not be blank"))
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun editRole_then200() {
        val request = RoleRequest("new name", "new description", listOf(1, 2, 3))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[2].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[2].description").isEmpty)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions", Matchers.hasSize<Any>(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[2].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[2].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[2].description").isEmpty)
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun editRole_lessPermissions_then200() {
        val request = RoleRequest("new name", "new description", listOf(1))
        mockMvc.perform(
            MockMvcRequestBuilders.put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role edited successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto.permissions[0].description").isEmpty)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("new name"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("new description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].description").isEmpty)

    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getRole_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getRole_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_ROLES"])
    @Throws(java.lang.Exception::class)
    fun getRole_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_ROLES"])
    @Throws(java.lang.Exception::class)
    fun getRole_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.permissions[1].description").isEmpty)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getRoles_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getRoles_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_ROLES"])
    @Throws(java.lang.Exception::class)
    fun getRoles_invalidCriteria_then400() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles?search=ide>0")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid key: 'ide'."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_ROLES"])
    @Throws(java.lang.Exception::class)
    fun getRoles_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles?search=id>0&sortBy=id&sortOrder=ASC")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].permissions", Matchers.hasSize<Any>(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].permissions[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].permissions[0].name").value("NAME1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].permissions[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].permissions[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].permissions[1].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].permissions[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].permissions", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].permissions[0].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].permissions[0].name").value("NAME2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].permissions[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].permissions", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].permissions[0].id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].permissions[0].name").value("NAME3"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].permissions[0].description").isEmpty)

    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_ROLES"])
    @Throws(java.lang.Exception::class)
    fun getRoles_withNarrowerCriteria_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles?search=id>2&sortBy=id&sortOrder=ASC")
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
    fun getAllRoles_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun getAllRoles_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/all")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_SEE_ROLES"])
    @Throws(java.lang.Exception::class)
    fun getAllRoles_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/roles/all")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").isEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].description").isEmpty)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun deleteRole_withoutAccount_then401() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    @WithMockUser(username = "user1")
    @Throws(java.lang.Exception::class)
    fun deleteRole_withoutPermissions_then403() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Access is denied"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }


    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun deleteRole_nonExistent_then404() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/roles/{id}", 69)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role not found by field id with value: 69"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }

    @Test
    @WithMockUser(username = "user1", authorities = ["CAN_EDIT_ROLES"])
    @Throws(java.lang.Exception::class)
    fun deleteRole_then200() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/roles/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Role deleted successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dto").doesNotExist())
    }
}