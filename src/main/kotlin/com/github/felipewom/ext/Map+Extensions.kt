package com.github.felipewom.ext

import com.github.felipewom.commons.ApiConstants
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties


fun Any?.toMap(): Map<String, Any>? {
    if (this == null) return null
    val objAsMap = hashMapOf<String, Any>()

    this::class.memberProperties.forEach { prop ->
        if (prop.visibility == KVisibility.PUBLIC) {
            val name = prop.name
            prop.getter.call(this)?.let { value ->
                if (value is String || value is Number || value is Boolean) {
                    objAsMap[name] = value
                }
            }
        }
    }

    return objAsMap
}


fun <K, V> Map<K, V>.asCookieString(): String {
    var str = ""
    for (map in this) {
        str += "${map.key}=${map.value.toString()};"
    }
    return str
}

fun groupArgs(vararg args: Any?): String {
    var groupedArgs = ""
    for (arg in args) {
        if (arg == null) {
            continue
        }
        groupedArgs += when {
            groupedArgs.isEmpty() -> "$arg"
            else -> "${com.github.felipewom.commons.ApiConstants.PARAM_SEPARATOR}$arg"
        }
    }
    return groupedArgs
}