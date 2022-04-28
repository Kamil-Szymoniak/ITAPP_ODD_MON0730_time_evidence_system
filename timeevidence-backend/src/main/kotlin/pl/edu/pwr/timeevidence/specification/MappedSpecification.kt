package pl.edu.pwr.timeevidence.specification

import pl.edu.pwr.timeevidence.LocalDateParser
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.criteria.*

abstract class MappedSpecification<T> : CriteriaSpecification<T> {
    private lateinit var criteria: FilterCriteria
    abstract fun getFieldMap(): Map<String, FieldInfo>

    override fun getGreaterThan(path: Expression<*>, query: CriteriaQuery<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate? {
        return when {
            path.javaClass == java.lang.String::class.java -> {
                builder.greaterThanOrEqualTo(path.`as`(String::class.java), criteria.value as String)
            }
            path.javaType == java.lang.Integer::class.java -> {
                builder.greaterThanOrEqualTo(path.`as`(Int::class.java), criteria.value.toString().toInt())
            }
            path.javaType == java.lang.Short::class.java -> {
                builder.greaterThanOrEqualTo(path.`as`(Short::class.java), criteria.value.toString().toShort())
            }
            path.javaType == java.lang.Long::class.java -> {
                builder.greaterThanOrEqualTo(path.`as`(Long::class.java), criteria.value.toString().toLong())
            }
            path.javaType == java.lang.Float::class.java -> {
                builder.greaterThanOrEqualTo(path.`as`(Float::class.java), criteria.value.toString().toFloat())
            }
            path.javaType == java.lang.Double::class.java -> {
                builder.greaterThanOrEqualTo(path.`as`(Double::class.java), criteria.value.toString().toDouble())
            }
            path.javaType == java.util.Date::class.java || path.javaType == java.sql.Date::class.java -> {
                builder
                    .greaterThanOrEqualTo(path.`as`(java.util.Date::class.java), LocalDateParser.parseDate(criteria.value.toString()))
            }
            path.javaType == LocalDate::class.java -> {
                builder
                    .greaterThanOrEqualTo(path.`as`(LocalDate::class.java), LocalDateParser.parseLocalDate(criteria.value.toString()))
            }
            path.javaType == LocalDateTime::class.java -> {
                builder
                    .greaterThanOrEqualTo(path.`as`(LocalDateTime::class.java), LocalDateParser.parseLocalDateTime(criteria.value.toString()))
            }
            else -> {
                null
            }
        }
    }

    override fun getLessThan(path: Expression<*>, query: CriteriaQuery<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate? {
        return when (path.javaType) {
            java.lang.String::class.java -> {
                builder.lessThanOrEqualTo(path.`as`(String::class.java), criteria.value.toString())
            }
            java.lang.Integer::class.java -> {
                builder.lessThanOrEqualTo(path.`as`(Int::class.java), criteria.value.toString().toInt())
            }
            java.lang.Short::class.java -> {
                builder.lessThanOrEqualTo(path.`as`(Short::class.java), criteria.value.toString().toShort())
            }
            java.lang.Long::class.java -> {
                builder.lessThanOrEqualTo(path.`as`(Long::class.java), criteria.value.toString().toLong())
            }
            java.lang.Float::class.java -> {
                builder.lessThanOrEqualTo(path.`as`(Float::class.java), criteria.value.toString().toFloat())
            }
            java.lang.Double::class.java -> {
                builder.lessThanOrEqualTo(path.`as`(Double::class.java), criteria.value.toString().toDouble())
            }
            java.util.Date::class.java, Date::class.java -> {
                builder
                    .lessThanOrEqualTo(path.`as`(java.util.Date::class.java), LocalDateParser.parseDate(criteria.value.toString()))
            }
            LocalDate::class.java -> {
                builder
                    .lessThanOrEqualTo(path.`as`(LocalDate::class.java), LocalDateParser.parseLocalDate(criteria.value.toString()))
            }
            LocalDateTime::class.java -> {
                builder
                    .lessThanOrEqualTo(path.`as`(LocalDateTime::class.java), LocalDateParser.parseLocalDateTime(criteria.value.toString()))
            }
            else -> null
        }
    }

    override fun getLikeOrEqual(path: Expression<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate? {
        return when {
            path.javaType == java.lang.String::class.java -> {
                builder.like(
                    builder.lower(path.`as`(String::class.java)), "%" + criteria.value.toString()
                        .lowercase(Locale.getDefault())
                        .replace("\\", "\\\\").replace("_", "\\_") + "%"
                )
            }
            path.javaType == java.lang.Integer::class.java -> {
                builder.equal(path, criteria.value.toString().toInt())
            }
            path.javaType == java.lang.Short::class.java -> {
                builder.equal(path, criteria.value.toString().toShort())
            }
            path.javaType == java.lang.Long::class.java -> {
                builder.equal(path, criteria.value.toString().toLong())
            }
            path.javaType == java.lang.Float::class.java -> {
                builder.equal(path, criteria.value.toString().toFloat())
            }
            path.javaType == java.lang.Double::class.java -> {
                builder.equal(path, criteria.value.toString().toDouble())
            }
            path.javaType == java.util.Date::class.java || path.javaType.isEnum -> {
                builder.equal(path.`as`(String::class.java), criteria.value)
            }
            path.javaType == LocalDate::class.java -> {
                builder
                    .equal(path.`as`(LocalDate::class.java), LocalDateParser.parseLocalDate(criteria.value.toString()))
            }
            path.javaType == LocalDateTime::class.java -> {
                builder
                    .equal(path.`as`(LocalDateTime::class.java), LocalDateParser.parseLocalDateTime(criteria.value.toString()))
            }
            path.javaType == java.lang.Boolean::class.java -> {
                builder.equal(path.`as`(Boolean::class.java), java.lang.Boolean.parseBoolean(criteria.value.toString()))
            }
            else -> builder.equal(path, criteria.value)
        }
    }

    override fun getSuchAs(root: Root<T>, path: Expression<*>, query: CriteriaQuery<*>, builder: CriteriaBuilder, criteria: FilterCriteria): Predicate? {
        return when {
            path.javaType == java.lang.String::class.java -> {
                val values = criteria.value.toString().split("\\|")
                if (getFieldInfo(criteria.key).isSet) getSetPredicate(values, root, query, builder, criteria)
                else builder.isTrue(path.`in`(values))
            }
            path.javaType == java.lang.Boolean::class.java -> {
                val values = criteria.value.toString().split("\\|").map { it.toBoolean() }
                builder.isTrue(path.`in`(values))
            }
            path.javaType.isEnum -> {
                val values = criteria.value.toString().split("\\|")
                builder.isTrue(path.`as`(String::class.java).`in`(values))
            }
            path.javaType == java.lang.Integer::class.java -> {
                val values = criteria.value.toString().split("\\|")
                    .map { try { it.toInt() } catch (_: RuntimeException) { -1 } }
                getSetPredicate(values, root, query, builder, criteria)
            }
            path.javaType == java.lang.Long::class.java -> {
                val values = criteria.value.toString().split("\\|")
                    .map { try { it.toLong() } catch (_: RuntimeException) { -1L } }
                getSetPredicate(values, root, query, builder, criteria)
            }
            path.javaType == java.lang.Short::class.java -> {
                val values = criteria.value.toString().split("_")
                    .map { try { it.toShort() } catch (_: RuntimeException) { -1 } }
                getSetPredicate(values, root, query, builder, criteria)
            }
            else -> null
        }
    }

    private fun getSetPredicate(values: List<Any>,
                                root: Root<T>,
                                query: CriteriaQuery<*>,
                                builder: CriteriaBuilder,
                                criteria: FilterCriteria
    ): Predicate? {
        query.distinct(true)
        var p: Predicate? = null
        if (criteria.searchType == SearchType.AND) {
            values.forEach {
                p = if (p == null) {
                    builder.equal(root.join<Any, Any>(criteria.key).get<Any>(getFieldInfo(criteria.key).setField), it)
                } else {
                    builder.and(builder.equal(root.join<Any, Any>(criteria.key).get<Any>(getFieldInfo(criteria.key).setField), it), p)
                }
            }
        } else {
            values.forEach {
                p = if (p == null) {
                    builder.equal(root.join<Any, Any>(criteria.key).get<Any>(getFieldInfo(criteria.key).setField), it)
                } else {
                    builder.or(builder.equal(root.join<Any, Any>(criteria.key).get<Any>(getFieldInfo(criteria.key).setField), it), p)
                }
            }
        }

        return p
    }

    override fun getCriteria() = criteria

    override fun setCriteria(criteria: FilterCriteria) {
        this.criteria = criteria
    }

    open fun shouldThrowException(field: String?): Boolean {
        return true
    }

    override fun getPredicatePath(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Expression<T> {
        val field: String = criteria.key
        val fieldInfo = getFieldInfo(field)
        require(fieldInfo.filter) { "Invalid key: '$field'." }
        var path: Path<T> = root
        for (subPath in fieldInfo.path) {
            path = path.get(subPath)
        }
        if (path.javaType == java.util.Set::class.java) {
            require(fieldInfo.isSet) { "Should not be a set: '$field'" }
            return root.join<Any?, Any?>(fieldInfo.path[fieldInfo.path.size - 1]).get(fieldInfo.setField)
        }

        return path
    }

    override fun getSorting(argument: String?): String? {
        if (argument == null) {
            return null
        }
        val fieldInfo = getFieldInfo(argument)
        require(fieldInfo.sort) { "Invalid key: '$argument'." }
        return fieldInfo.path.joinToString(separator = "_")
    }

    protected fun getFieldInfo(field: String?): FieldInfo {
        val fieldInfo = getFieldMap()[field]
        require(!(fieldInfo == null && shouldThrowException(field))) { "Invalid key: '$field'." }
        return fieldInfo!!
    }

    protected open fun fieldMap(vararg fields: FieldInfo): Map<String, FieldInfo> {
        return fields.asList().associateBy { it.field }
    }

    open fun field(field: String, vararg path: String): FieldInfo {
        return FieldInfo(field, listOf(*path))
    }
}