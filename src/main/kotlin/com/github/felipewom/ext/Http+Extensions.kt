package com.github.felipewom.ext

import com.github.felipewom.commons.ResultHandler
import com.github.kittinunf.fuel.core.Response
import org.eclipse.jetty.http.HttpStatus


fun <T> Response.failureWith(): ResultHandler<T> = tryResult {
    return when (this.statusCode) {
        401 -> ResultHandler.failure("${this.body().asString(com.github.felipewom.commons.ApiConstants.JSON_MIME)} -> ${HttpStatus.UNAUTHORIZED_401} ${this.responseMessage}")
        404 -> ResultHandler.failure("${this.body().asString(com.github.felipewom.commons.ApiConstants.JSON_MIME)} -> ${HttpStatus.NOT_FOUND_404} ${this.responseMessage}")
        400 -> ResultHandler.failure("${this.body().asString(com.github.felipewom.commons.ApiConstants.JSON_MIME)} -> ${HttpStatus.BAD_REQUEST_400} ${this.responseMessage}")
        else -> ResultHandler.failure("${this.body().asString(com.github.felipewom.commons.ApiConstants.JSON_MIME)} -> ${this.statusCode} ${this.responseMessage}")
    }
}