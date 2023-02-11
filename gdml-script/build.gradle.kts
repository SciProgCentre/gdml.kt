plugins {
    id("space.kscience.gradle.jvm")
    `maven-publish`
    application
}

kscience{
    application()
}

dependencies {
    implementation(rootProject)
    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-jvm"))
    implementation(spclibs.logback.classic)
    implementation(spclibs.kotlinx.cli)
}

application{
    mainClass.set("space.kscience.gdml.script.CliKt")
}

readme{
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}

