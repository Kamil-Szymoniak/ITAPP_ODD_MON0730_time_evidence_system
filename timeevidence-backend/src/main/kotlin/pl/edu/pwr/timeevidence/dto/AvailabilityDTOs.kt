package pl.edu.pwr.timeevidence.dto

import pl.edu.pwr.timeevidence.entity.AvailabilityEntity
import pl.edu.pwr.timeevidence.exception.BadRequestException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.validation.constraints.FutureOrPresent

data class PeriodRequest(
    val timeFrom: LocalTime,
    val timeTo: LocalTime
) {
   companion object {
       fun toString(periods: List<PeriodRequest>): String {
           for (i in 0 until periods.size - 1) {
               for (j in i until periods.size) {
                   if ((
                          periods[i].timeFrom.isBefore(periods[j].timeTo)
                       && periods[i].timeFrom.isAfter(periods[j].timeFrom))
                       || (periods[i].timeTo.isBefore(periods[j].timeTo)
                       && periods[i].timeTo.isAfter(periods[j].timeFrom))) {
                       throw BadRequestException("Period should not start or end during another one")
                   }
               }
           }
           return periods.joinToString("_") {
               "${it.timeFrom}-${it.timeTo}-${Duration.between(it.timeFrom, it.timeTo).toMinutes()}"
           }
       }
   }
}

data class PeriodResponse(
    val timeFrom: LocalTime,
    val timeTo: LocalTime,
    val minutes: Int
) {
    companion object {
        fun fromString(string: String): List<PeriodResponse> {
            return string.split("_").map { setOfTimes ->
                val times = setOfTimes.split("-")
                PeriodResponse(LocalTime.parse(times[0]), LocalTime.parse(times[1]), times[2].toInt())
            }
        }
    }
}

data class AvailabilityRequest(
    val comment: String?,
    @field:FutureOrPresent
    val date: LocalDate,
    val team: Int,
    val periods: List<PeriodRequest>
)

data class AvailabilityResponse(
    val id: Int,
    val comment: String?,
    val date: LocalDate,
    val person: DictionaryResponse,
    val team: DictionaryResponse,
    val periods: List<PeriodResponse>
) {
    companion object {
        fun fromEntity(entity: AvailabilityEntity) = AvailabilityResponse(
            id = entity.id!!,
            comment = entity.comment,
            date = entity.date,
            person = DictionaryResponse.fromPerson(entity.person!!),
            team = DictionaryResponse.fromTeam(entity.team!!),
            periods = PeriodResponse.fromString(entity.periods)
        )
    }
}