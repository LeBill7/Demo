package com.autopulse.app.core.logging

import java.time.Instant
import java.util.concurrent.ConcurrentLinkedDeque

enum class LogLevel(val priority: Int) {
    DEBUG(10),
    INFO(20),
    WARN(30),
    ERROR(40)
}

data class LogEntry(
    val timestamp: String,
    val level: LogLevel,
    val module: String,
    val message: String,
    val context: Map<String, String> = emptyMap()
)

object StructuredLogger {
    private val sensitiveKeys = setOf(
        "password",
        "token",
        "secret",
        "apikey",
        "api_key",
        "authorization",
        "ssn",
        "privatekey"
    )

    private const val MAX_BUFFER_SIZE = 250
    private val logBuffer = ConcurrentLinkedDeque<LogEntry>()

    @Volatile
    var minLevel: LogLevel = LogLevel.DEBUG

    fun log(
        level: LogLevel,
        module: String,
        message: String,
        context: Map<String, Any?> = emptyMap()
    ): LogEntry {
        val sanitizedContext = sanitize(context)
        val entry = LogEntry(
            timestamp = Instant.now().toString(),
            level = level,
            module = module,
            message = message,
            context = sanitizedContext
        )

        if (level.priority >= minLevel.priority) {
            logBuffer.addLast(entry)
            while (logBuffer.size > MAX_BUFFER_SIZE) {
                logBuffer.pollFirst()
            }
        }
        return entry
    }

    fun debug(module: String, message: String, context: Map<String, Any?> = emptyMap()): LogEntry =
        log(LogLevel.DEBUG, module, message, context)

    fun info(module: String, message: String, context: Map<String, Any?> = emptyMap()): LogEntry =
        log(LogLevel.INFO, module, message, context)

    fun warn(module: String, message: String, context: Map<String, Any?> = emptyMap()): LogEntry =
        log(LogLevel.WARN, module, message, context)

    fun error(module: String, message: String, context: Map<String, Any?> = emptyMap()): LogEntry =
        log(LogLevel.ERROR, module, message, context)

    fun getRecentLogs(limit: Int = 50): List<LogEntry> =
        logBuffer.toList().takeLast(limit).reversed()

    fun clearLogs() {
        logBuffer.clear()
    }

    private fun sanitize(raw: Map<String, Any?>): Map<String, String> {
        if (raw.isEmpty()) return emptyMap()
        val result = LinkedHashMap<String, String>(raw.size)
        for ((key, value) in raw) {
            val normalizedKey = key.lowercase().trim()
            if (sensitiveKeys.any { normalizedKey.contains(it) }) {
                result[key] = "[REDACTED]"
            } else {
                result[key] = value?.toString() ?: "null"
            }
        }
        return result
    }
}
