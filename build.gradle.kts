plugins {
    id("scientifik.mpp") version "0.1.3"
    id("scientifik.publish") version "0.1.3"
    id("kotlinx-serialization") version "1.3.41"
}

group = "scientifik"
version = "0.1.1"

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
                api("net.devrieze:xmlutil-serialization:0.11.1.2-SNAPSHOT")
                api(kotlin("reflect"))
            }
        }
    }

    targets.all {
        sourceSets.all {
            languageSettings.apply{
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
            }
        }
    }
}

scientifik {
    bintrayRepo = "scientifik"
    githubProject = "gdml.kt"
}