package com.autopulse.app

import android.app.Application
import com.autopulse.app.core.logging.StructuredLogger
import com.autopulse.app.core.version.AppVersion

class AutoPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StructuredLogger.info(
            module = "AutoPulseApplication",
            message = "Initialized ${AppVersion.APP_NAME} v${AppVersion.VERSION_NAME} Android runtime"
        )
    }
}
