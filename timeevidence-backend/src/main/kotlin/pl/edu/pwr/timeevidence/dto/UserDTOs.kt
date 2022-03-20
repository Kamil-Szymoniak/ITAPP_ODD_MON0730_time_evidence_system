package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.entity.UserEntity
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserRequest (
    @field:NotBlank
    val username: String,
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
    val roles: List<Short>,
    val person: Int? = null
)

data class LoginRequest (
    @field:NotBlank
    val login: String,
    @field:NotBlank
    val password: String
)

data class ChangePasswordRequest (
    @field:NotBlank
    val oldPassword: String,
    @field:NotBlank
    val newPassword: String
)

data class UserResponse (
    val id: Int,
    val username: String,
    val email: String,
    val person: DictionaryResponse?,
    val roles: List<DictionaryResponse>
) {
    companion object {
        fun fromEntity(entity: UserEntity) = UserResponse(
            id = entity.id!!,
            username = entity.username,
            email = entity.email,
            person = entity.person?.let { DictionaryResponse.fromPerson(it) },
            roles = entity.roles.map { DictionaryResponse.fromRole(it) }
        )
    }
}

data class MeResponse (
    val username: String,
    val email: String,
    val person: PersonResponse?
) {
    companion object {
        fun fromEntity(entity: UserEntity) = MeResponse(
            username = entity.username,
            email = entity.email,
            person = entity.person?.let { PersonResponse.fromEntity(it) }
        )
    }
}