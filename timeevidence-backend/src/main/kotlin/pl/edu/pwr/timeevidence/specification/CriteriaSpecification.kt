package pl.edu.pwr.timeevidence.specification

import org.springframework.data.jpa.domain.Specification
import org.springframework.lang.NonNull
import pl.edu.pwr.timeevidence.exception.BadRequestException
import javax.persistence.criteria.*

interface CriteriaSpecification<T> : Specification<T> {

    fun setCriteria(criteria: FilterCriteria)
    fun getCriteria(): FilterCriteria

    fun getPredicatePath(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Expression<*>
    fun getGreaterThan(path: Expression<*>, query: CriteriaQuery<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate?
    fun getLessThan(path: Expression<*>, query: CriteriaQuery<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate?
    fun getLikeOrEqual(path: Expression<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate?
    fun getSuchAs(root: Root<T>, path: Expression<*>, query: CriteriaQuery<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate?

    fun getSorting(argument: String?): String?

    override fun toPredicate(
        @NonNull root: Root<T>,
        @NonNull query: CriteriaQuery<*>,
        @NonNull criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val criteria: FilterCriteria = getCriteria()
        try {
            val path = getPredicatePath(root, query, criteriaBuilder)
            when (criteria.operation) {
                ">" -> {
                    return getGreaterThan(path, query, criteriaBuilder, criteria)
                }
                "<" -> {
                    return getLessThan(path, query, criteriaBuilder, criteria)
                }
                ":" -> {
                    return getLikeOrEqual(path, criteriaBuilder, criteria)
                }
                "~" -> {
                    return getSuchAs(root, path, query, criteriaBuilder, criteria)
                }
            }
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message)
        }
        return null
    }
}