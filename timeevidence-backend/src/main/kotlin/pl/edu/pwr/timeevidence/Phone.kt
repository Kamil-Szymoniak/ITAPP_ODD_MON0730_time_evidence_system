package pl.edu.pwr.timeevidence

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [PhoneValidator::class])
annotation class Phone(
    val message: String = "The field must be a well formed phone number",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PhoneValidator : ConstraintValidator<Phone, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return try {
            "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*\$".toRegex().matches(value!!)
        } catch (_: Exception) {
            false
        }
    }
}
