plugins {
    id("scientifik.mpp") version "0.1.7"
    id("scientifik.publish") version "0.1.7"
}

group = "scientifik"
version = "0.1.4-dev-2"

scientifik {
    withSerialization()
}

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
                api("net.devrieze:xmlutil-serialization:0.12.0.0")
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