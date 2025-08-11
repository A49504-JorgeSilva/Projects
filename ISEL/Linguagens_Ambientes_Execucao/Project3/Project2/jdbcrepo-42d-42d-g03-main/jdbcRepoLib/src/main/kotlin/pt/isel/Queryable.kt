package pt.isel

import kotlin.reflect.KProperty1

interface Queryable<T> : Sequence<T> {
    fun <V> whereEquals(
        prop: KProperty1<T, V>,
        value: V,
    ): Queryable<T>

    fun <V> orderBy(prop: KProperty1<T, V>): Queryable<T>
}
