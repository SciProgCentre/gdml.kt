import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import java.util.*

plugins {
    id("scientifik.mpp") version "0.1.4"
    //id("scientifik.publish") version "0.1.4"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

group = "scientifik"
version = "0.1.3"

scientifik {
    serialization = true
}

val githubProject by extra("gdml.kt")
val bintrayRepo by extra("scientifik")
val vcs by extra("https://github.com/mipt-npm/gdml.kt")


repositories {
    maven("https://dl.bintray.com/pdvrieze/maven")
}

kotlin {
    js {
        browser {
            testTask {
                useKarma{
                    useChrome()
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("net.devrieze:xmlutil-serialization:0.11.1.2")
                api(kotlin("reflect"))
            }
        }
    }

    targets.all {
        sourceSets.all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
            }
        }
    }
}

publishing {
    repositories {
        maven("https://bintray.com/mipt-npm/$bintrayRepo")
    }
}

bintray {
    user = project.findProperty("bintrayUser") as? String?
    key = project.findProperty("bintrayApiKey") as? String?
    publish = true
    override = true

    pkg.apply {
        userOrg = "mipt-npm"
        repo = bintrayRepo
        name = project.name
        issueTrackerUrl = "$vcs/issues"
        setLicenses("Apache-2.0")
        vcsUrl = vcs
        version.apply {
            name = project.version.toString()
            vcsTag = project.version.toString()
            released = Date().toString()
        }
    }

    //workaround bintray bug
    setPublications(*project.extensions.findByType<PublishingExtension>()!!.publications.names.toTypedArray())
}

afterEvaluate {
    tasks.withType<BintrayUploadTask>(){
        doFirst {
            publishing.publications.filterIsInstance<MavenPublication>()
                .forEach { publication ->
                    val moduleFile =
                        buildDir.resolve("publications/${publication.name}/module.json")
                    if (moduleFile.exists()) {
                        publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
                            override fun getDefaultExtension() = "module"
                        })
                    }
                }
        }
    }
}