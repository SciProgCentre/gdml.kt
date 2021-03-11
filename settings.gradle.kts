pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
        maven("https://repo.kotlin.link")
    }
}

rootProject.name = "gdml"
include("gdml-script")
