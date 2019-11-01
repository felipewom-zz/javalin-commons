package com.github.felipewom.ext

import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope


fun getEnvironmentProp(key: String): String? {
    val prop = System.getenv(key)
    if (prop.isNotNullOrBlank()) {
        return prop
    }
    return System.getProperty(key, null)
}

inline fun <reified T> getVariable(key: String): T? {
    val variable = getEnvironmentProp(key) ?: GlobalContext.get().koin.getProperty<T?>(key)
    return variable as T?
}

fun getEnvProp(scope: Scope, defaultValue: Int, vararg keys: String): Int {
    return try {
        checkVariables(
            scope,
            defaultValue,
            keys
        ) { it.toString().toInt() }
    } catch (e: Exception) {
        defaultValue
    }
}

fun getEnvProp(scope: Scope, defaultValue: String, vararg keys: String): String {
    return try {
        checkVariables(
            scope,
            defaultValue,
            keys
        ) { it.toString() }
    } catch (e: Exception) {
        defaultValue
    }
}

fun getEnvProp(scope: Scope, defaultValue: Boolean, vararg keys: String): Boolean {
    return try {
        checkVariables(
            scope,
            defaultValue,
            keys
        ) { java.lang.Boolean.parseBoolean(it.toString()) }
    } catch (e: Exception) {
        defaultValue
    }
}

inline fun <reified T> checkVariables(
    scope: Scope,
    defaultValue: T,
    keys: Array<out String>,
    function: (found: Any) -> T
): T {
    for (key in keys) {
        val systemProp = getVariable<T>(key)
        val applicationProp = scope.getPropertyOrNull<T>(key)
        val found = when {
            systemProp != null && systemProp.toString().isNotBlank() -> systemProp
            applicationProp != null -> applicationProp
            else -> null
        }
        if (found != null) {
            return function(found)
        }
    }
    return defaultValue
}