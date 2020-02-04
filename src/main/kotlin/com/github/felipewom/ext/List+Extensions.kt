package com.github.felipewom.ext

import kotlin.math.min


/**
 * returns a view (not a new list) of the source list for the
 * range based on pageNumber and pageSize
 * @param pageNumber, pageNumber number should start from 0
 * @param pageSize
 * @return page
 */
fun <T> List<T>.getPage(pageNumber: Int, pageSize: Int): List<T> {
    require(!(pageNumber < 0 || pageSize <= 0)) { "invalid page size: $pageSize || invalid page number: $pageNumber, parameter must be pageSize >= 0 || pageNumber > 0" }

    val fromIndex = pageNumber * pageSize
    return if (this.size < fromIndex) {
        emptyList()
    } else this.subList(fromIndex, min(fromIndex + pageSize, this.size))
}