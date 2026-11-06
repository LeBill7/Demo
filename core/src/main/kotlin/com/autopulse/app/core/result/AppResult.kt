package com.autopulse.app.core.result

import com.autopulse.app.core.logging.StructuredLogger

sealed class AppError(
    open val code: String,
    override val message: String,
    open val details: Map<String, String> = emptyMap()
) : Exception(message) {

    data class ValidationError(
        override val message: String,
        override val details: Map<String, String> = emptyMap()
    ) : AppError("VALIDATION_ERROR", message, details)

    data class NotFoundError(
        override val message: String,
        override val details: Map<String, String> = emptyMap()
    ) : AppError("NOT_FOUND_ERROR", message, details)

    data class NetworkError(
        override val message: String,
        override val details: Map<String, String> = emptyMap()
    ) : AppError("NETWORK_ERROR", message, details)

    data class IntegrityTamperError(
        override val message: String,
        override val details: Map<String, String> = emptyMap()
    ) : AppError("INTEGRITY_TAMPER_ERROR", message, details)

    data class InternalError(
        override val message: String,
        override val details: Map<String, String> = emptyMap()
    ) : AppError("INTERNAL_ERROR", message, details)
}

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): AppError? = (this as? Failure)?.error
}

inline fun <T> runCatchingLogged(
    module: String,
    operation: String,
    block: () -> T
): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: AppError) {
        StructuredLogger.error(
            module = module,
            message = "Domain error in $operation: ${e.message}",
            context = mapOf("code" to e.code) + e.details
        )
        AppResult.Failure(e)
    } catch (t: Throwable) {
        val wrapped = AppError.InternalError(
            message = t.message ?: "Unexpected failure in $operation",
            details = mapOf("exception" to t::class.simpleName.orEmpty())
        )
        StructuredLogger.error(
            module = module,
            message = "Unhandled exception in $operation",
            context = mapOf("code" to wrapped.code, "reason" to wrapped.message)
        )
        AppResult.Failure(wrapped)
    }
}
