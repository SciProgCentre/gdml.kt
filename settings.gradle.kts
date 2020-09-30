pluginManagement {
    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/mipt-npm/dev")
        maven("https://dl.bintray.com/mipt-npm/kscience")
    }
}

enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "gdml"

