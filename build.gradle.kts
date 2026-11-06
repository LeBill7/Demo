import java.util.Properties

plugins {
    id("com.android.application") version "8.7.2" apply false
    kotlin("jvm") version "2.1.0" apply false
    kotlin("android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}

val versionProps = Properties().apply {
    val file = rootProject.file("version.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

allprojects {
    group = "com.autopulse.app"
    version = versionProps.getProperty("VERSION_NAME", "1.1.0")
}

tasks.register("checkLineLimits") {
    group = "verification"
    description = "Enforces GEMINI.md Rule #2: No source file may exceed 500 lines of code."
    doLast {
        val maxLines = 500
        val targetExtensions = setOf("kt", "kts", "xml", "yml", "yaml", "md")
        val excludedDirs = setOf(".git", ".gradle", "build", "dist-release", ".idea")

        val violations = mutableListOf<String>()
        var checkedCount = 0

        rootProject.projectDir.walkTopDown()
            .onEnter { dir -> dir.name !in excludedDirs }
            .filter { it.isFile && it.extension in targetExtensions }
            .forEach { file ->
                checkedCount++
                val lineCount = file.readLines().size
                if (lineCount > maxLines) {
                    val rel = file.relativeTo(rootProject.projectDir).path
                    violations.add("$rel ($lineCount lines > $maxLines limit)")
                }
            }

        if (violations.isNotEmpty()) {
            throw GradleException(
                "500-Line File Limit violated in ${violations.size} file(s):\n" +
                    violations.joinToString("\n") { " - $it" }
            )
        }
        println("[OK] Verified $checkedCount project files — all strictly <= $maxLines lines.")
    }
}

tasks.register<Zip>("releaseBundle") {
    group = "distribution"
    description = "Packages compiled AutoPulse APK, core JAR, and docs for GitHub Release."
    dependsOn(":core:build", ":app:assembleDebug", "checkLineLimits")
    archiveBaseName.set("autopulse-android")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("release-bundle")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))

    from(project(":core").layout.buildDirectory.dir("libs")) {
        into("libs")
    }
    from(project(":app").layout.buildDirectory.dir("outputs/apk/debug")) {
        include("*.apk")
        into("apk")
    }
    from("README.md", "CHANGELOG.md", "GEMINI.md", "version.properties")
    from("docs") {
        into("docs")
    }
}
