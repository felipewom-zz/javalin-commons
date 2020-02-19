@file:JvmName("ResponseError")
package com.github.felipewom.commons

open class ResponseError(val errors: Map<String, List<String?>>){
    fun errorMessages(): String {
        val list: MutableList<String> = mutableListOf()
        for (error in errors) {
            list.add("${error.key}: ${error.value.joinToString(", ")}")
        }
        return list.joinToString("; ")
    }
}
