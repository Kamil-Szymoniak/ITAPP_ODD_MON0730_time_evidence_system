package pl.edu.pwr.timeevidence.dto

import org.springframework.data.domain.Page

data class PagedResponse<T> (val items: List<T> = emptyList(), val totalElements: Long = 0) {
    constructor(items: List<T> = emptyList()) : this(items, items.size.toLong())
    constructor(items: Page<T>) : this(items.content, items.totalElements)
}
