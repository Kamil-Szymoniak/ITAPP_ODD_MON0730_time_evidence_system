package pl.edu.pwr.timeevidence

import pl.edu.pwr.timeevidence.exception.BadRequestException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.math.min

class LocalDateParser {
    companion object {
        fun parseDate(date: String): Date? {
            val format = SimpleDateFormat(if (date.length == 10) "yyyy-MM-dd" else "yyyy-MM-dd hh-mm")
            return try {
                format.parse(date)
            } catch (e: ParseException) {
                throw BadRequestException("Could not parse the date from query")
            }
        }

        fun parseLocalDate(date: String?): LocalDate? {
            return try {
                if (date == null) null else LocalDate.parse(date.substring(0, min(10, date.length)))
            } catch (e: DateTimeParseException) {
                throw BadRequestException("The date string $date failed to be parsed")
            }
        }

        fun parseLocalDate(date: Date?): LocalDate? {
            return try {
                if (date == null) null else Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()
            } catch (e: DateTimeParseException) {
                throw BadRequestException("The date $date failed to be parsed")
            }
        }

        fun parseLocalDateTime(date: String?): LocalDateTime? {
            return try {
                if (date == null) null else LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm"))
            } catch (e: DateTimeParseException) {
                throw BadRequestException("The date string $date failed to be parsed")
            }
        }

        fun parseLocalDateTime(date: Date?): LocalDateTime? {
            return try {
                if (date == null) null else Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
            } catch (e: DateTimeParseException) {
                throw BadRequestException("The date $date failed to be parsed")
            }
        }
    }
}