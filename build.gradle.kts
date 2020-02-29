import scientifik.useSerialization

plugins {
    id("scientifik.mpp") version "0.3.2"
    id("scientifik.publish") version "0.3.2"
}

group = "scientifik"
version = "0.1.6"

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
                api("net.devrieze:xmlutil-serialization:0.14.0.2")
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