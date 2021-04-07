pluginManagement {
    plugins {
        kotlin("multiplatform") version "1.4.32"
        id("org.jetbrains.compose") version "0.4.0-build179"
    }

    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "PathCompose"
