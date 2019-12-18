import scientifik.useSerialization

plugins {
    id("scientifik.mpp") version "0.3.1"
    id("scientifik.publish") version "0.3.1"
}

group = "scientifik"
version = "0.1.4"

project.useSerialization()

repositories {
    maven("https://dl.bintray.com/pdvrieze/maven")
}

kotlin {
    js {
        browser {
            testTask {
                useKarma {
                    useChrome()
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("net.devrieze:xmlutil-serialization:0.14.0.1")
                api(kotlin("reflect"))
            }
        }
    }

    targets.all {
        sourceSets.all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
            }
        }
    }
}