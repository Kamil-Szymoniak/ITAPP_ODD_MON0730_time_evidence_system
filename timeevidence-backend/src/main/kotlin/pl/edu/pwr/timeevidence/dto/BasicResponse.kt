package pl.edu.pwr.timeevidence.dto

data class BasicResponse(val message: String, val success: Boolean, val dto: Any? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasicResponse

        if (message != other.message) return false
        if (success != other.success) return false
        if (dto != other.dto) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + success.hashCode()
        result = 31 * result + (dto?.hashCode() ?: 0)
        return result
    }
}