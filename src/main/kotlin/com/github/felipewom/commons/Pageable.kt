@file:JvmName("Pageable")
package com.github.felipewom.commons

import com.github.felipewom.ext.isNotNullOrBlank
import com.github.felipewom.ext.tryOrNull
import com.github.felipewom.utils.GsonUtils.deserialize
import com.google.gson.annotations.Expose

/**
 * Data class for pagination information.
 * @author Felipe Moura
 */
open class Pageable<DTO>(@Expose(deserialize = false) var totalSize: Int = 0 ) {

    constructor() : this(0)

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
     * @return
     */
    var result: List<DTO> = emptyList()

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

    /**
     * Returns the DTO requested.
     * @return
     */
    fun <DTO> getObjectFilter(clzz: Class<DTO>): DTO? = tryOrNull {
        if (objectFilter != null) {
            return deserialize(objectFilter!!, clzz)
        }
        return null
    }
}
