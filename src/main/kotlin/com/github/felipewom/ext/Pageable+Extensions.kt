package com.github.felipewom.ext

import com.github.felipewom.commons.Pageable
import com.github.felipewom.utils.gson.GsonUtils

fun Pageable.limit(): Int {
    return try {
        (this.pageNumber + 1) * this.pageSize
    } catch (_: Exception) {
        10
    }
}

/**
 * Returns the DTO requested.
 * @return
 */
inline fun <reified DTO:Any> Pageable.getObjectFilter(): DTO? = tryOrNull {
    if (objectFilter != null) {
        return GsonUtils.deserialize(objectFilter!!, DTO::class.java)
    }
    return null
}

/**
 * Returns the DTO requested.
 * @return
 */
inline fun <reified DTO:Any> Pageable.getObjectFilterMap(): Map<String, Any>? = tryOrNull {
    if (objectFilter != null) {
        return GsonUtils.deserialize(objectFilter!!, DTO::class.java)?.toMap()
    }
    return null
}