@file:JvmName("Pageable")

package com.github.felipewom.commons

import com.github.felipewom.ext.isNotNullOrBlank
import io.javalin.http.Context

/**
 * Data class for pagination information.
 * @author Felipe Moura
 */
open class Pageable {

    constructor()
    constructor(ctx: Context) : this() {
        this.pageNumber = ctx.queryParam(PageableFields.PAGE_NUMBER)?.toInt() ?: 1
        this.pageSize = ctx.queryParam(PageableFields.PAGE_SIZE)?.toInt() ?: 20
        this.orderBy = ctx.queryParam(PageableFields.ORDER_BY)
        this.filter = ctx.queryParam(PageableFields.FILTER)
        this.objectFilter = ctx.queryParam(PageableFields.OBJECT_FILTER)
        this.totalSize = 0
    }

    var totalSize: Int = 0

    /**
     * Page number requested.
     * @return the page to be returned.
     */
    var pageNumber: Int = 0
        set(value) {
            if (value <= 0) {
                field = 0
                return
            }
            field = value
        }

    /**
     * Size of items per page.
     * @return the number of items of that page
     */
    var pageSize: Int = 10
        set(value) {
            if (value == 0) {
                field = 10
                return
            }
            field = value
        }

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     * @return the offset to be taken
     */
    val offset: Int
        get() {
            if (pageNumber == 0) {
                return pageNumber * pageSize
            }
            return (pageNumber - 1) * pageSize
        }

    /**
     * Returns the order parameters name.
     * @return
     */
    var orderBy: String? = null
        set(value) {
            if (value.isNotNullOrBlank()) {
                field = value
                return
            }
            field = null
        }

    /**
     * Returns the list of DTO requested.
     * @return List<Any>
     */
    var result: List<Any> = emptyList()

    /**
     * Returns the serialized DTO requested.
     * @return
     */
    var objectFilter: String? = null
        set(value) {
            if (value.isNotNullOrBlank()) {
                field = value
                return
            }
            field = null
        }

    /**
     * Returns the query string requested.
     * @return
     */
    var filter: String? = null
        set(value) {
            if (value.isNotNullOrBlank()) {
                field = value
                return
            }
            field = null
        }

}
