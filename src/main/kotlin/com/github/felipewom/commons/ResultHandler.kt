@file:JvmName("ResultHandler")
package com.github.felipewom.commons

import java.io.Serializable

@Suppress("UNCHECKED_CAST")
data class ResultHandler<out T : Any?>(val value: T?) {

    /**
     * Returns `true` if this instance represents successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = value !is Failure

    /**
     * Returns `true` if this instance represents failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = value is Failure

    /**
     * Returns result value if this instance represents successful outcome.
     * In this case [isFailure] returns null.
     */
    val result: Result<T>
        get() = when (value) {
            is Failure -> Result(
                value = null
            )
            else -> Result(value as T)
        }

    operator fun component2() = this.result

    /**
     * Returns `true` if this instance represents failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val error: Failure?
        get() = when (value) {
            is Failure -> Failure(
                message = value.message,
                throwable = value.throwable
            )
            else -> null
        }

    operator fun component3() = this.error
    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][ResultHandler.isSuccess] or `null`
     * if it is [failure][ResultHandler.isFailure].
     *
     * This function is shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? = when {
        isFailure -> null
        else -> value as T
    }

    fun get(): T = value as T

    /**
     * Returns the encapsulated exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? = when (value) {
        is Failure -> value.throwable
        else -> null
    }

    fun exceptionMessageOrNull(): String? = when (value) {
        is Failure -> value.message
        else -> null
    }

    fun exceptionMessage(): String = when (value) {
        is Failure -> value.message ?: "Unkown error"
        else -> "ResultHandler Success"
    }

    /**
     * Returns a string `Success(v)` if this instance represents [success][ResultHandler.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String = when (value) {
        is Failure -> value.toString() // "Failure($exception)"
        else -> "Success($value)"
    }

    // companion with constructors

    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <T> success(value: T): ResultHandler<T> = ResultHandler(value)

        /**
         * Returns an instance that encapsulates the given [exception] as failure.
         */
        fun <T> failure(exception: Throwable): ResultHandler<T> =  ResultHandler<T>(com.github.felipewom.ext.createFailure<T>(exception))

        /**
         * Returns an instance that encapsulates the given [exception] as failure.
         */
        fun <T> failure(message: String? = "Error"): ResultHandler<T> = ResultHandler<T>(com.github.felipewom.ext.createFailure<T>(message))

    }

    class Result<out T : Any?>(
        @JvmField val value: T? = null
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Result<*> && value == other.value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Result($value)"
    }

    data class Failure(
        @JvmField val message: String? = null, @JvmField val throwable: Throwable? = null
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Failure && throwable == other.throwable
        override fun hashCode(): Int = throwable.hashCode()
        override fun toString(): String = "Failure(${throwable?.printStackTrace()})"
    }
}
