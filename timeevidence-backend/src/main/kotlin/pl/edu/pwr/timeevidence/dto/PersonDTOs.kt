package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.Phone
import pl.edu.pwr.timeevidence.entity.PersonEntity
import java.time.LocalDate
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Past

data class PersonRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val surname: String,
    @field:Phone
    val phone: String?,
    @field:Past
    val birthday: LocalDate
)

data class PersonResponse(
    val id: Int,
    val name: String,
    val surname: String,
    val phone: String?,
    val birthday: LocalDate,
    val user: DictionaryResponse?
) {
    companion object {
        fun fromEntity(entity: PersonEntity) = PersonResponse(
            id = entity.id!!,
            name = entity.name,
            surname = entity.surname,
            phone = entity.phone,
            birthday = entity.birthday,
            user = entity.user?.let { DictionaryResponse.fromUser(it) }
        )
    }
}