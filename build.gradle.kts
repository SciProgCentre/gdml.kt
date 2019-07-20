plugins {
    id("scientifik.mpp") version "0.1.4-dev"
    id("scientifik.publish") version "0.1.4-dev"
}

group = "scientifik"
version = "0.1.3"

scientifik{
    bintrayRepo = "scientifik"
    githubProject = "gdml.kt"
    serialization = true
}

repositories {
    maven("https://dl.bintray.com/pdvrieze/maven")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("net.devrieze:xmlutil-serialization:0.11.1.2")
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