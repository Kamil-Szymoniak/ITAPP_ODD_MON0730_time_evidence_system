package pl.edu.pwr.timeevidence.specification

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryRequest(
    val specification: KClass<*>,
    val defaultPageIndex: Int = 0,
    val defaultPageSize: Int = 10
)
