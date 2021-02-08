plugins {
    val toolsVersion = "0.7.6"
    id("ru.mipt.npm.project") version toolsVersion
    id("ru.mipt.npm.mpp") version toolsVersion
//    id("ru.mipt.npm.native") version toolsVersion
    id("ru.mipt.npm.publish") version toolsVersion
}

group = "kscience.gdml"
version = "0.2.0"

kscience {
    useSerialization("1.0.1") {
        xml()
    }
}

repositories {
    maven("https://dl.bintray.com/pdvrieze/maven")
}

ksciencePublish {
    githubProject = "gdml.kt"
    bintrayRepo = "kscience"
}

val bintrayRepo by extra("kscience")
val githubProject by extra("gdml.kt")

kotlin {

    sourceSets {
        commonMain {
            dependencies {
//                api(kotlin("reflect"))
            }
        }

        jvmMain {
            dependencies {
                api("com.fasterxml.woodstox:woodstox-core:5.0.3")
            }
        }
    }
}