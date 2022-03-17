package pl.edu.pwr.timeevidence.exception

class NotFoundException(message: String?) : RuntimeException(message) {
    constructor(resource: String, field: String, value: Any)
            : this(String.format("%s not found by field %s with value: %s", resource, field, value))
}