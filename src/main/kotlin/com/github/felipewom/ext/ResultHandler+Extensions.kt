package com.github.felipewom.ext

import com.github.felipewom.commons.ResultHandler
import com.github.felipewom.commons.logger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun justTry(block: () -> Unit) = try {
    block()
} catch (e: Exception) {
    logger.info("JUSTTRY::EXCEPTION => ${e.message}")
}

inline fun <T> tryOrNull(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    logger.info("TRYORNULL::EXCEPTION => ${e.message}")
    null
}

inline fun <T> tryResult(block: () -> ResultHandler<T>): ResultHandler<T> = try {
    block()
} catch (e: Exception) {
    logger.info("TRYRESULT::EXCEPTION => ${e.message}")
    ResultHandler.failure<T>(e.message)
}

inline fun tryAndCatch(block: () -> Unit, catch: (throwable: Throwable) -> Unit): Unit = try {
    block()
} catch (e: Exception) {
    logger.info("TRYANDCATCH::EXCEPTION => ${e.message}")
    catch(e)
}

inline fun <T> trySimpleResult(block: () -> T): ResultHandler<T> = try {
    ResultHandler.success(block())
} catch (e: Exception) {
    logger.info("TRYSIMPLERESULT::EXCEPTION => ${e.message}")
    ResultHandler.failure<T>(e.message)
}

/**
 * Creates an instance of internal marker [ResultHandler.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
@PublishedApi
@Suppress("UNCHECKED_CAST")
internal fun <T> createFailure(throwable: Throwable): T = ResultHandler.Failure(
    throwable = throwable
) as T

@PublishedApi
@Suppress("UNCHECKED_CAST")
internal fun <T> createFailure(message: String?): T = ResultHandler.Failure(message = message) as T

/**
 * Throws exception if the result is failure. This internal function minimizes
 * inlined bytecode for [getOrThrow] and makes sure that in the future we can
 * add some exception-augmenting logic here (if needed).
 */
@PublishedApi
internal fun ResultHandler<*>.throwOnFailure() {
    logger.info("TRYSIMPLERESULT::EXCEPTION => ${this.exceptionMessageOrNull()}")
    if (value is ResultHandler.Failure && value.throwable != null) throw value.throwable
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching and encapsulating any thrown exception as a failure.
 */
@SinceKotlin("1.3")
inline fun <R> runCatching(block: () -> R): ResultHandler<R> {
    return try {
        ResultHandler.success(block())
    } catch (e: Throwable) {
        logger.info("RUNCATCHING::EXCEPTION => ${e.message}")
        ResultHandler.failure(e)
    }
}

/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated result
 * if invocation was successful, catching and encapsulating any thrown exception as a failure.
 */
@SinceKotlin("1.3")
inline fun <T, R> T.runCatching(block: T.() -> R): ResultHandler<R> {
    return try {
        ResultHandler.success(block())
    } catch (e: Throwable) {
        logger.info("RUNCATCHING::EXCEPTION => ${e.message}")
        ResultHandler.failure(e)
    }
}

// -- extensions ---

/**
 * Returns the encapsulated value if this instance represents [success][ResultHandler.isSuccess] or throws the encapsulated exception
 * if it is [failure][ResultHandler.isFailure].
 *
 * This function is shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
@Suppress("UNCHECKED_CAST")
fun <T> ResultHandler<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

/**
 * Returns the encapsulated value if this instance represents [success][ResultHandler.isSuccess] or the
 * result of [onFailure] function for encapsulated exception if it is [failure][ResultHandler.isFailure].
 *
 * Note, that an exception thrown by [onFailure] function is rethrown by this function.
 *
 * This function is shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
@ExperimentalContracts
@Suppress("UNCHECKED_CAST")
inline fun <R, T : R> ResultHandler<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][ResultHandler.isSuccess] or the
 * [defaultValue] if it is [failure][ResultHandler.isFailure].
 *
 * This function is shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
@Suppress("UNCHECKED_CAST")
fun <R, T : R> ResultHandler<T>.getOrDefault(defaultValue: R): R {
    if (isFailure) return defaultValue
    return value as T
}

/**
 * Returns the the result of [onSuccess] for encapsulated value if this instance represents [success][ResultHandler.isSuccess]
 * or the result of [onFailure] function for encapsulated exception if it is [failure][ResultHandler.isFailure].
 *
 * Note, that an exception thrown by [onSuccess] or by [onFailure] function is rethrown by this function.
 */
@ExperimentalContracts
@Suppress("UNCHECKED_CAST")
inline fun <R, T> ResultHandler<T>.fold(
    onSuccess: (value: T) -> R, onFailure: (exception: Throwable) -> R
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}

// transformation

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated value
 * if this instance represents [success][ResultHandler.isSuccess] or the
 * original encapsulated exception if it is [failure][ResultHandler.isFailure].
 *
 * Note, that an exception thrown by [transform] function is rethrown by this function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
@ExperimentalContracts
@Suppress("UNCHECKED_CAST")
inline fun <R, T> ResultHandler<T>.map(transform: (value: T) -> R): ResultHandler<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when {
        isSuccess -> ResultHandler.success(transform(value as T))
        else -> ResultHandler.failure("Error while performing map")
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated value
 * if this instance represents [success][ResultHandler.isSuccess] or the
 * original encapsulated exception if it is [failure][ResultHandler.isFailure].
 *
 * Any exception thrown by [transform] function is caught, encapsulated as a failure and returned by this function.
 * See [map] for an alternative that rethrows exceptions.
 */
@Suppress("UNCHECKED_CAST")
inline fun <R, T> ResultHandler<T>.mapCatching(transform: (value: T) -> R): ResultHandler<R> {
    return when {
        isSuccess -> runCatching { transform(value as T) }
        else -> ResultHandler.failure("Error while performing mapCatching")
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated exception
 * if this instance represents [failure][ResultHandler.isFailure] or the
 * original encapsulated value if it is [success][ResultHandler.isSuccess].
 *
 * Note, that an exception thrown by [transform] function is rethrown by this function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
@ExperimentalContracts
@SinceKotlin("1.3")
inline fun <R, T : R> ResultHandler<T>.recover(transform: (exception: Throwable) -> R): ResultHandler<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> ResultHandler.success(transform(exception))
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to encapsulated exception
 * if this instance represents [failure][ResultHandler.isFailure] or the
 * original encapsulated value if it is [success][ResultHandler.isSuccess].
 *
 * Any exception thrown by [transform] function is caught, encapsulated as a failure and returned by this function.
 * See [recover] for an alternative that rethrows exceptions.
 */
@SinceKotlin("1.3")
inline fun <R, T : R> ResultHandler<T>.recoverCatching(transform: (exception: Throwable) -> R): ResultHandler<R> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatching { transform(exception) }
    }
}

// "peek" onto value/exception and pipe

/**
 * Performs the given [action] on encapsulated exception if this instance represents [failure][ResultHandler.isFailure].
 * Returns the original `Result` unchanged.
 */
@ExperimentalContracts
inline fun <T> ResultHandler<T>.onFailure(action: (exception: Throwable) -> Unit): ResultHandler<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on encapsulated value if this instance represents [success][ResultHandler.isSuccess].
 * Returns the original `Result` unchanged.
 */
@ExperimentalContracts
@Suppress("UNCHECKED_CAST")
inline fun <T> ResultHandler<T>.onSuccess(action: (value: T) -> Unit): ResultHandler<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (isSuccess) action(value as T)
    return this
}

