plugins {
    val toolsVersion = "0.9.5"
    id("ru.mipt.npm.gradle.project") version toolsVersion
    id("ru.mipt.npm.gradle.mpp") version toolsVersion
//    id("ru.mipt.npm.gradle.native") version toolsVersion
    `maven-publish`
}

allprojects {
    group = "space.kscience"
    version = "0.4.0"
    repositories {
        mavenCentral()
        maven("https://repo.kotlin.link")
    }
}

kscience {
    useSerialization()
}

ksciencePublish {
    github("gdml.kt")
    space()
    sonatype()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("io.github.pdvrieze.xmlutil:serialization:0.82.0")
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.2")
            }
        }
        jvmMain {
            dependencies {
                implementation("com.fasterxml.woodstox:woodstox-core:6.2.3")
            }
        }
    }
}

readme {
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
}

changelog{
    version = project.version.toString()
}