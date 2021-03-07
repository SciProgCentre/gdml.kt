plugins {
    val toolsVersion = "0.9.1"
    id("ru.mipt.npm.gradle.project") version toolsVersion
    id("ru.mipt.npm.gradle.mpp") version toolsVersion
//    id("ru.mipt.npm.gradle.native") version toolsVersion
    id("ru.mipt.npm.gradle.publish") version toolsVersion
}

group = "space.kscience"
version = "0.3.1"

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

readme {
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
}