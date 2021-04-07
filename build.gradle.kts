import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "org.example"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        jcenter()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://dl.bintray.com/kyonifer/maven" )
    }
}

kotlin {
    jvm()

    sourceSets {
        named("commonMain") {
            dependencies {
                api("com.kyonifer:koma-core-api-common:0.12")
            }
        }

        named("jvmMain") {
            dependencies {
                implementation("com.kyonifer:koma-core-ejml:0.12")

                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "PathCompose"
            packageVersion = "0.1.0"
        }
    }
}