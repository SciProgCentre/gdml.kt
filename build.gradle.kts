import space.kscience.gradle.Maturity
import space.kscience.gradle.useApache2Licence
import space.kscience.gradle.useSPCTeam

plugins {
    id("space.kscience.gradle.project")
    id("space.kscience.gradle.mpp")
    `maven-publish`
}

allprojects {
    group = "space.kscience"
    version = "0.5.1"
}

kscience {
    jvm()
    js()
    native()
    commonMain {
        implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
    }
    jvmMain {
        implementation("com.fasterxml.woodstox:woodstox-core:7.2.2")
        implementation("io.github.pdvrieze.xmlutil:core-jdk:${spclibs.versions.xmlutil.get()}")
    }
    useSerialization {
        xml()
    }
}

kscienceProject {
    pom("https://github.com/SciProgCentre/gdml.kt") {
        useApache2Licence()
        useSPCTeam()
    }
    publishTo("spc", "https://maven.sciprog.center/kscience")
    publishToCentral()
}



readme {
    maturity = Maturity.DEVELOPMENT
}