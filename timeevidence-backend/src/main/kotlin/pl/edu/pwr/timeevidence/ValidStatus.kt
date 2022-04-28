package pl.edu.pwr.timeevidence

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [ValidStatusValidator::class])
annotation class ValidStatus(
    val message: String = "The field must be either 'ACCEPTED' or 'REJECTED'",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidStatusValidator : ConstraintValidator<ValidStatus, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext?): Boolean {
        return try {
            value == "ACCEPTED" || value == "REJECTED"
        } catch (_: Exception) {
            false
        }
    }
}
