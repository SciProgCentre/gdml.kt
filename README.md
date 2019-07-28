# GDML bindings for Kotlin

[ ![Download](https://api.bintray.com/packages/mipt-npm/scientifik/gdml/images/download.svg) ](https://bintray.com/mipt-npm/scientifik/gdml/_latestVersion)

Multiplatform bindings for GDML geometry specification. Utilized kotlinx.serialization
to read (**does not work yet**) and writing GDML configurations.

In future it is possible to add additional module to launch GEANT4 simulations from Kotlin code.

The artifacts could be accessed via following configurations: 
```kotlin
repository{
    jcenter()
    maven("https://dl.bintray.com/mipt-npm/scientifik")
    maven("https://dl.bintray.com/pdvrieze/maven")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

//for multiplatform with gradle-metadata:
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("scientifik:gdml:0.1.3")
            }
        }
    }
}

//for jvm
dependencies {
    api("scientifik:gdml-jvm:0.1.3")
}
```
