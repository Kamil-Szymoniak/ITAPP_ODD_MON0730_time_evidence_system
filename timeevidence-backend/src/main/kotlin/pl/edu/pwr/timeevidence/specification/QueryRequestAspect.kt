package pl.edu.pwr.timeevidence.specification

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.util.UriComponentsBuilder
import pl.edu.pwr.timeevidence.exception.BadRequestException
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@Aspect
@Component
class QueryRequestAspect {
    @Throws(Throwable::class)
    @Around(value = "@annotation(queryRequest)")
    fun injectCriteria(join: ProceedingJoinPoint, queryRequest: QueryRequest): Any? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes)
            .request
        val params = UriComponentsBuilder
            .fromUriString(request.requestURI + "?" + request.queryString)
            .build()
            .queryParams
        val pageNumber = getPageNumber(params.getFirst("pageNumber"), queryRequest)
        val pageSize = getPageSize(params.getFirst("pageSize"), queryRequest)
        val sortBy = getSortBy(params.getFirst("sortBy"), queryRequest)
        val sortOrder = getSortOrder(params.getFirst("sortOrder"))
        val searchType = getCompositionType(params.getFirst("searchType"))
        val specification = getSpecification(params.getFirst("search"), queryRequest, searchType)
        val visibleColumns = getVisibleColumns(params.getFirst("visibleColumns"))
        val sorting = getSorting(sortBy, sortOrder)
        val paging = getPaging(pageNumber, pageSize, sorting)
        val criteria = EntityCriteria(specification, paging, visibleColumns)
        val arguments = join.args
        val argumentsWithoutCriteria = Arrays
            .copyOfRange(arguments, 0, arguments.size - 1)
        val newArguments = Stream
            .concat(Arrays.stream(argumentsWithoutCriteria), Stream.of(criteria))
            .toArray()
        return join.proceed(newArguments)
    }

    private fun getSorting(sortBy: String?, sortDirection: Sort.Direction): Sort {
        return if (sortBy != null) {
            if (sortDirection.isDescending) {
                Sort.by(Sort.Order.desc(sortBy).ignoreCase())
            } else {
                Sort.by(Sort.Order.asc(sortBy).ignoreCase())
            }
        } else Sort.unsorted()
    }

    private fun getPaging(pageNumber: Int, pageSize: Int, sorting: Sort): Pageable =
        PageRequest.of(pageNumber, if (pageSize == -1) Int.MAX_VALUE else pageSize, sorting)

    private fun getVisibleColumns(param: String?): List<String>? =
        if (param != null) listOf(*param.split(",").toTypedArray()) else null

    private fun getSpecification(
        param: String?,
        queryRequest: QueryRequest,
        searchType: SearchType
    ): Specification<*>? {
        val supplier = getSupplier(queryRequest.specification)!!
        val builder = SpecificationBuilder<Any, CriteriaSpecification<Any>>(supplier, searchType)
        return builder.buildFromQuery(param)
    }

    private fun getCompositionType(param: String?): SearchType = if (param != null) {
        try {
            SearchType.valueOf(param.uppercase(Locale.getDefault()))
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("The given search type is invalid.")
        }
    } else SearchType.AND

    private fun getPageNumber(param: String?, queryRequest: QueryRequest): Int =
        param?.toInt() ?: queryRequest.defaultPageIndex

    private fun getPageSize(param: String?, queryRequest: QueryRequest): Int =
        param?.toInt() ?: queryRequest.defaultPageSize

    private fun getSortOrder(param: String?): Sort.Direction = if (param != null) {
        try {
            Sort.Direction.valueOf(param.uppercase(Locale.getDefault()))
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("The given sorting order method is invalid.")
        }
    } else Sort.Direction.DESC

    private fun getSortBy(param: String?, queryRequest: QueryRequest): String? = try {
        (getSupplier(queryRequest.specification))?.getSorting(param)
    } catch (e: IllegalArgumentException) {
        throw BadRequestException(e.message)
    }

    private fun getSupplier(specificationClass: KClass<*>): CriteriaSpecification<*>? {
            try {
                return specificationClass.primaryConstructor!!.call() as CriteriaSpecification<*>
            } catch (_: InstantiationException) {

            } catch (_: IllegalAccessException) {

            } catch (_: InvocationTargetException) {

            } catch (_: NoSuchMethodException) {

            }
        return null
    }
}