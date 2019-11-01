@file:JvmName("HttpClient")
package com.github.felipewom.utils

import com.github.felipewom.commons.ApiConstants
import com.github.felipewom.commons.ResultHandler
import com.github.felipewom.ext.failureWith
import com.github.felipewom.ext.tryResult
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.httpGet

class HttpClient {
    init {
        FuelManager.instance.baseHeaders = mapOf(com.github.felipewom.commons.ApiConstants.CONTENT_TYPE_JSON, com.github.felipewom.commons.ApiConstants.ACCEPT_JSON)
    }

    fun <T> get(
        url: String,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        jwtToken: String? = null,
        ssoToken: String? = null,
        clzz: Class<T>
    ): ResultHandler<T> = tryResult {
        val req = url.httpGet()
        headers?.let { req.header(it) }
        jwtToken?.let {
            req.authentication().bearer(it)
        }
        ssoToken?.let {
            req.appendHeader(com.github.felipewom.commons.ApiConstants.COOKIE, it)
        }
        params?.let {
            req.parameters = params.toList()
        }
        val (_, response, result) = req.responseString()
        if (!response.isSuccessful) {
            return response.failureWith()
        }
        val bodyResp = result.get()
        val obj =
            GsonUtils.deserialize(result.get(), clzz) ?: return ResultHandler.failure(
                "Falha ao deserializar $bodyResp"
            )
        return ResultHandler.success(obj)
    }

    fun get(
        url: String,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        jwtToken: String? = null,
        ssoToken: String? = null
    ): ResultHandler<String?> = tryResult {
        val req = url.httpGet()
        headers?.let { req.header(it) }
        jwtToken?.let {
            req.authentication().bearer(it)
        }
        ssoToken?.let {
            req.appendHeader(com.github.felipewom.commons.ApiConstants.COOKIE, it)
        }
        params?.let {
            req.parameters = params.toList()
        }
        val (_, response, result) = req.responseString()
        if (!response.isSuccessful) {
            return response.failureWith()
        }
        val bodyResp = result.get()
        return ResultHandler.success(bodyResp)
    }

    fun <T> post(
        url: String,
        body: Any,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        jwtToken: String? = null,
        ssoToken: String? = null,
        clzz: Class<T>
    ): ResultHandler<T> = tryResult {
        val req = Fuel.post(url)
        req.jsonBody(body)
        headers?.let { req.header(it) }
        jwtToken?.let {
            req.authentication().bearer(it)
        }
        ssoToken?.let {
            req.appendHeader(com.github.felipewom.commons.ApiConstants.COOKIE, it)
        }
        params?.let {
            req.parameters = params.toList()
        }
        val (_, response, result) = req.responseString()
        if (!response.isSuccessful) {
            return response.failureWith()
        }
        val bodyResp = result.get()
        val obj =
            GsonUtils.deserialize(result.get(), clzz) ?: return ResultHandler.failure(
                "Falha ao deserializar $bodyResp"
            )
        return ResultHandler.success(obj)
    }

    fun post(
        url: String,
        body: Any,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        jwtToken: String? = null,
        ssoToken: String? = null
    ): ResultHandler<String?> = tryResult {
        val req = Fuel.post(url)
        req.jsonBody(body)
        headers?.let { req.header(it) }
        jwtToken?.let {
            req.authentication().bearer(it)
        }
        ssoToken?.let {
            req.appendHeader(com.github.felipewom.commons.ApiConstants.COOKIE, it)
        }
        params?.let {
            req.parameters = params.toList()
        }
        val (_, response, result) = req.responseString()
        if (!response.isSuccessful) {
            return response.failureWith()
        }
        val bodyResp = result.get()
        return ResultHandler.success(bodyResp)
    }

    fun <T> delete(
        url: String,
        body: Any?,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        jwtToken: String? = null,
        ssoToken: String? = null,
        clzz: Class<T>
    ): ResultHandler<T> = tryResult {
        val req = Fuel.delete(url)
        if (body != null) {
            req.jsonBody(body)
        }
        headers?.let { req.header(it) }
        jwtToken?.let {
            req.authentication().bearer(it)
        }
        ssoToken?.let {
            req.appendHeader(com.github.felipewom.commons.ApiConstants.COOKIE, it)
        }
        params?.let {
            req.parameters = params.toList()
        }
        val (_, response, result) = req.responseString()
        if (!response.isSuccessful) {
            return response.failureWith()
        }
        val bodyResp = result.get()
        val obj =
            GsonUtils.deserialize(result.get(), clzz) ?: return ResultHandler.failure(
                "Falha ao deserializar $bodyResp"
            )
        return ResultHandler.success(obj)
    }

    fun delete(
        url: String,
        body: Any?,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        jwtToken: String? = null,
        ssoToken: String? = null
    ): ResultHandler<String?> = tryResult {
        val req = Fuel.delete(url)
        if (body != null) {
            req.jsonBody(body)
        }
        headers?.let { req.header(it) }
        jwtToken?.let {
            req.authentication().bearer(it)
        }
        ssoToken?.let {
            req.appendHeader(com.github.felipewom.commons.ApiConstants.COOKIE, it)
        }
        params?.let {
            req.parameters = params.toList()
        }
        val (_, response, result) = req.responseString()
        if (!response.isSuccessful) {
            return response.failureWith()
        }
        val bodyResp = result.get()
        return ResultHandler.success(bodyResp)
    }
}

