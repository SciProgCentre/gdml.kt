
plugins {
    id("ru.mipt.npm.mpp") version "0.6.0-dev-3"
    id("ru.mipt.npm.publish") version "0.6.0-dev-3"
}

group = "ru.mipt.npm"
version = "0.2.0"

kscience {
    useSerialization {
        xml()
    }
}

repositories{
    mavenLocal()
}

val bintrayRepo by extra("kscience")
val githubProject by extra("gdml.kt")

kotlin {
    js(IR) {
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
//                api(kotlin("reflect"))
            }
        }

        jvmMain{
            dependencies {
                api("com.fasterxml.woodstox:woodstox-core:5.0.3")
            }
        }
    }
}