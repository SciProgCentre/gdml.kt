pluginManagement {
    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/mipt-npm/dev")
        maven("https://dl.bintray.com/mipt-npm/scientifik")
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlinx-atomicfu" -> useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
                "kscience.mpp", "kscience.publish" -> useModule("ru.mipt.npm:gradle-tools:${requested.version}")
            }
        }
    }
}

enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "gdml"

