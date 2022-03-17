package pl.edu.pwr.timeevidence.dto

data class FieldError (
    private val field: String,
    private val message: String?
)