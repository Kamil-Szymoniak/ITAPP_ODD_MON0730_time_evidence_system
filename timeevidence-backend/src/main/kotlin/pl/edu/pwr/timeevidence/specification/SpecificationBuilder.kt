package pl.edu.pwr.timeevidence.specification

import org.apache.commons.text.StringEscapeUtils
import org.springframework.data.jpa.domain.Specification
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

@Suppress("UNCHECKED_CAST")
class SpecificationBuilder<T, E : CriteriaSpecification<T>>(private val supplier: Any, private val searchType: SearchType? = null){

    fun buildFromQuery(query: String?): Specification<T>? {
        if (query == null) {
            return null
        }
        val params: MutableList<FilterCriteria> = ArrayList()
        val matcher = Pattern.compile("([\\w%\\-.]+?)([:<>~=])([\\w%\\- +.\\\\/]+),?")
            .matcher(URLDecoder.decode(query, StandardCharsets.UTF_8) + ",")
        while (matcher.find()) {
            try {
                params.add(
                    FilterCriteria(
                        matcher.group(1),
                        matcher.group(2),
                        StringEscapeUtils.unescapeJava(matcher.group(3)),
                        searchType ?: SearchType.AND
                    )
                )
            } catch (e: Exception) {
                params.add(
                    FilterCriteria(
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3),
                        searchType ?: SearchType.OR
                    )
                )
            }
        }
        if (params.isEmpty()) {
            return null
        }
        val specifications: List<Specification<T>> = params.map {
            val specification: E = supplier as E
            specification.setCriteria(it)
            specification
        }.toList()
        var result = specifications[0]
        if (searchType == null || searchType == SearchType.AND) {
            for (i in 1 until params.size) {
                result = Specification.where(result).and(specifications[i])
            }
        } else {
            for (i in 1 until params.size) {
                result = Specification.where(result).or(specifications[i])
            }
        }
        return result
    }
}