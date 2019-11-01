@file:JvmName("ErrorResponse")
package com.github.felipewom.commons

data class ErrorResponse(val errors: Map<String, List<String?>>)