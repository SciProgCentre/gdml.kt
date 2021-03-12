plugins {
    val toolsVersion = "0.9.2"
    id("ru.mipt.npm.gradle.project") version toolsVersion
    id("ru.mipt.npm.gradle.mpp") version toolsVersion
//    id("ru.mipt.npm.gradle.native") version toolsVersion
    id("ru.mipt.npm.gradle.publish") version toolsVersion
}

group = "space.kscience"
version = "0.3.4-dev-1"

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/pdvrieze/maven")
    }
}

kscience {
    useSerialization {
        xml()
    }
}

ksciencePublish{
    github("gdml.kt")
    space()
    sonatype()
}

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