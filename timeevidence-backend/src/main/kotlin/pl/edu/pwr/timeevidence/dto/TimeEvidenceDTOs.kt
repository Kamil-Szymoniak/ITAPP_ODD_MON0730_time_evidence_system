package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.ValidStatus
import pl.edu.pwr.timeevidence.entity.TimeEvidenceEntity
import java.time.LocalDate
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Positive

data class TimeEvidenceRequest(
    @field:PastOrPresent
    val date: LocalDate,
    @field:Positive
    val minutes: Short,
    val comment: String?,
    val project: Int
)

data class TimeEvidenceResponse(
    val id: Int,
    val date: LocalDate,
    val minutes: Short,
    val comment: String?,
    val person: DictionaryResponse,
    val project: DictionaryResponse,
    val status: String,
    val statusComment: String?
) {
    companion object {
        fun fromEntity(entity: TimeEvidenceEntity) = TimeEvidenceResponse(
            id = entity.id!!,
            date = entity.date,
            minutes = entity.minutes,
            comment = entity.comment,
            person = DictionaryResponse.fromPerson(entity.person!!),
            project = DictionaryResponse.fromProject(entity.project!!),
            status = entity.status,
            statusComment = entity.statusComment
        )
    }
}

data class TimeEvidenceChangeStatusRequest(
    @field:ValidStatus
    val status: String,
    val statusComment: String?
)