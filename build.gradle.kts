
plugins {
    id("kscience.mpp") version "0.6.0"
    id("kscience.publish") version "0.6.0"
}

group = "ru.mipt.npm"
version = "0.2.0"

kscience {
    useSerialization {
        xml()
    }
}

val bintrayRepo by extra("kscience")
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