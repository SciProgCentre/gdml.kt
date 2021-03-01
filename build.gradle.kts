plugins {
    val toolsVersion = "0.8.3"
    id("ru.mipt.npm.gradle.project") version toolsVersion
    id("ru.mipt.npm.gradle.mpp") version toolsVersion
//    id("ru.mipt.npm.native") version toolsVersion
    id("ru.mipt.npm.gradle.publish") version toolsVersion
}

group = "space.kscience"
version = "0.3.0"

kscience {
    useSerialization{
        xml()
    }
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/pdvrieze/maven")
}

internal val githubProject by extra("gdml.kt")
internal val spaceRepo by extra("https://maven.pkg.jetbrains.space/mipt-npm/p/sci/maven")
internal val bintrayRepo by extra("kscience")

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api("com.fasterxml.woodstox:woodstox-core:6.2.3")
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.1")
            }
        }
    }
}