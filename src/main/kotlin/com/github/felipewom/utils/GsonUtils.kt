@file:JvmName("GsonUtils")
package com.github.felipewom.utils

import com.github.felipewom.commons.logger
import com.github.felipewom.ext.ISO8601_PATTERN
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken

object GsonUtils {
    @JvmStatic
    val gson: Gson by lazy {
        GsonBuilder().addSerializationExclusionStrategy(serializationStrategy)
            .addDeserializationExclusionStrategy(deserializationStrategy)
            .disableInnerClassSerialization()
            .enableComplexMapKeySerialization().setDateFormat(ISO8601_PATTERN).create()
    }

    private var serializationStrategy: ExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipField(field: FieldAttributes): Boolean {
            for (ann in field.annotations) {
                return when (ann) {
                    is Expose -> !ann.serialize
                    else -> false
                }
            }
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return false
        }
    }

    private var deserializationStrategy: ExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipField(field: FieldAttributes): Boolean {
            for (ann in field.annotations) {
                return when (ann) {
                    is Expose -> !ann.deserialize
                    else -> false
                }
            }
            return false
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return false
        }
    }

    @JvmStatic
    inline fun <reified DTO : Any> deserialize(str: String): DTO? = try {
        val type = object : TypeToken<DTO>() {}.type
        gson.fromJson(str, type)
    } catch (e: Exception) {
        logger.error("[ERROR]GsonUtils::deserialize=>${e.message}")
        null
    }

    @JvmStatic
    fun <DTO> deserialize(str: String, clzz: Class<DTO>): DTO? = try {
        gson.fromJson(str, clzz)
    } catch (e: Exception) {
        logger.error("[ERROR]GsonUtils::deserialize=>${e.message}")
        null
    }

    @JvmStatic
    fun serialize(obj: Any): String? = try {
        gson.toJson(obj)
    } catch (e: Exception) {
        logger.error("[ERROR]GsonUtils::serialize=>${e.message}")
        null
    }
}