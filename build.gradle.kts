import space.kscience.gradle.Maturity
import space.kscience.gradle.isInDevelopment
import space.kscience.gradle.useApache2Licence
import space.kscience.gradle.useSPCTeam

plugins {
    id("space.kscience.gradle.project")
    id("space.kscience.gradle.mpp")
//    id("ru.mipt.npm.gradle.native") version toolsVersion
    `maven-publish`
}

allprojects {
    group = "space.kscience"
    version = "0.5.0"
}

kscience {
    jvm()
    js ()
    commonMain {
        implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
    }
    jvmMain {
        implementation("com.fasterxml.woodstox:woodstox-core:6.5.0")
    }
    useSerialization{
        xml()
    }
}

ksciencePublish {
    pom("https://github.com/SciProgCentre/gdml.kt") {
        useApache2Licence()
        useSPCTeam()
    }
    github(githubProject = "gdml.kt", githubOrg = "SciProgCentre")
    space(
        if (isInDevelopment) {
            "https://maven.pkg.jetbrains.space/spc/p/sci/dev"
        } else {
            "https://maven.pkg.jetbrains.space/spc/p/sci/maven"
        }
    )
    sonatype()
}


readme {
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
    maturity = Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
}