@file:JvmName("Roles")
package com.github.felipewom.security

import io.javalin.core.security.Role

enum class Roles : Role {
    ANYONE, AUTHENTICATED
}
