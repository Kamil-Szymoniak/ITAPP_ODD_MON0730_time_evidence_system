package pl.edu.pwr.timeevidence.specification

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification

enum class SearchType {
    AND, OR
}

data class FilterCriteria(
    val key: String,
    val operation: String,
    val value: Any,
    val searchType: SearchType? = null
)

data class EntityCriteria<T>(
    val specification: Specification<T>?,
    val paging: Pageable? = PageRequest.of(0, 15),
    val visibleColumns: List<String>?
)

class FieldInfo(
    val field: String,
    val path: List<String>,
    var sort: Boolean = true,
    var filter: Boolean = true,
    var isSet: Boolean = false,
    var setField: String = "",
) {
    fun noSort() = FieldInfo(field, path, sort = false)
    fun noFilter() = FieldInfo(field, path, filter = false)
    fun isSet(field: String) = FieldInfo(this.field, path, isSet = true, setField = field)
}