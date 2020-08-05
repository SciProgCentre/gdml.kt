import scientifik.useSerialization

plugins {
    id("scientifik.mpp") version "0.5.2"
    id("scientifik.publish") version "0.5.2"
}

group = "scientifik"
version = "0.1.8"

useSerialization {
    xml()
}

val bintrayRepo by extra("scientifik")
val githubProject by extra("gdml.kt")

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
        commonMain{
            dependencies {
                api(kotlin("reflect"))
            }
        }

        jvmMain{
            dependencies {
                api("com.fasterxml.woodstox:woodstox-core:5.0.3")
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