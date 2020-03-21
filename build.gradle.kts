import scientifik.serialization

plugins {
    id("scientifik.mpp") version "0.4.0"
    id("scientifik.publish") version "0.4.0"
}

group = "scientifik"
version = "0.1.7"

serialization {
    xml()
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
        commonMain{
            dependencies {
                api(kotlin("reflect"))
            }
        }

        jvmMain{
            dependencies {
                runtimeOnly("com.fasterxml.woodstox:woodstox-core:5.0.3")
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