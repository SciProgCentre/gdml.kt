plugins {
    id("ru.mipt.npm.gradle.jvm")
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
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
}

application{
    mainClassName = "space.kscience.gdml.script.CliKt"
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}