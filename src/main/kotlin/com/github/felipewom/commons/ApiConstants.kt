@file:JvmName("ApiConstants")
package com.github.felipewom.commons

import com.github.felipewom.ext.getEnvironmentProp

object ApiConstants {

    /**
     * AUTH CONSTANTS
     * */
    const val JWT_AUTHORIZATION_HEADER = "Authorization"
    const val JWT_BEARER_TOKEN = "Bearer"
    const val JWT_CLAIM_ID_ATTR = "id"
    const val JWT_CLAIM_EMAIL_ATTR = "email"
    const val JWT_SUBJECT_ATTR = "token"
    const val JWT_ROLE_ATTR = "role"
    const val TENANT_KEY_HEADER = "X-Server-Key"
    const val JWT_ROLE_AUTHENTICATED = "AUTHENTICATED"
    /**
     * Application Constants
     * */
    const val OVERVIEW_PATH = "/overview"
    const val TZ_SAO_PAULO = "America/Sao_Paulo"
    const val TZ_UTC = "UTC"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val ID: String = "id"
    const val PARAM_SEPARATOR = "§" // ¤ ∞
    @JvmStatic
    val ROOT_PACKAGE = getEnvironmentProp("ROOT_PACKAGE") ?: "com.github.felipewom"
    @JvmStatic
    val DB_SCHEMA = getEnvironmentProp("DB_SCHEMA")

    /*
    * HTTP Constants
    * */
    const val ACCEPT = "Accept"
    const val ACCEPT_CHARSET = "Accept-Charset"
    const val ACCEPT_ENCODING = "Accept-Encoding"
    const val ACCEPT_LANGUAGE = "Accept-Language"
    const val ACCEPT_RANGES = "Accept-Ranges"
    const val ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials"
    const val ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers"
    const val ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods"
    const val ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"
    const val ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers"
    const val ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age"
    const val ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers"
    const val ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method"
    const val AGE = "Age"
    const val ALLOW = "Allow"
    const val AUTHORIZATION = "Authorization"
    const val CACHE_CONTROL = "Cache-Control"
    const val CONNECTION = "Connection"
    const val CONTENT_ENCODING = "Content-Encoding"
    const val CONTENT_DISPOSITION = "Content-Disposition"
    const val CONTENT_LANGUAGE = "Content-Language"
    const val CONTENT_LENGTH = "Content-Length"
    const val CONTENT_LOCATION = "Content-Location"
    const val CONTENT_RANGE = "Content-Range"
    const val USER_AGENT = "User-Agent"
    const val CONTENT_TYPE = "Content-Type"
    const val COOKIE = "Cookie"
    const val SESSION_COOKIE = "__session"
    const val API_VERSION_HEADER = "Api-Version"
    const val JSON_MIME = "application/json"
    val ACCEPT_JSON = Pair(
        ACCEPT, JSON_MIME
    )
    val CONTENT_TYPE_JSON = Pair(
        CONTENT_TYPE,
        JSON_MIME
    )

    /*HTTP STATUS*/
    const val BAD_REQUEST_400 = "BAD_REQUEST_400"
    const val UNAUTHORIZED_401 = "UNAUTHORIZED_401"
}
