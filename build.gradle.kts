plugins {
    id("scientifik.mpp") version "0.1.0"
    id("scientifik.publish") version "0.1.0"
    id("kotlinx-serialization") version "1.3.40"
}

group = "scietifik"
version = "0.1.0"

val bintrayRepo by extra("scientifik")
val vcs by extra("https://github.com/mipt-npm/gdml.kt")

repositories {
    mavenLocal()
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
    maven("http://npm.mipt.ru:8081/artifactory/gradle-dev-local")
    maven("https://dl.bintray.com/pdvrieze/maven")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("net.devrieze:xmlutil-serialization:0.11.1.1")
                api(kotlin("reflect"))
            }
        }
    }
}

