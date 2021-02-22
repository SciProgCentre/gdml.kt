# Gdml bindings for Kotlin

[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/195530015.svg)](https://zenodo.org/badge/latestdoi/195530015)
[ ![Download](https://api.bintray.com/packages/mipt-npm/scientifik/gdml/images/download.svg) ](https://bintray.com/mipt-npm/scientifik/gdml/_latestVersion)

Multiplatform bindings for Gdml geometry specification. Utilized kotlinx.serialization
to read and writing Gdml configurations.

In future it is possible to add additional module to launch GEANT4 simulations from Kotlin code.

The artifacts could be accessed via following configurations: 
```kotlin
repository{
    jcenter()
    maven("https://repo.kotlin.link")
}

//for multiplatform with gradle-metadata:
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("scientifik:gdml:0.2.0")
            }
        }
    }
}

//for jvm
dependencies {
    api("scientifik:gdml-jvm:0.2.0")
}
```

## Usage
Read: 
```kotlin
import space.kscience.gdml.decodeFromString

val gdmlString: String
val gdml = Gdml.decodeFromString(gdmlString)
```

write:
```kotlin
import space.kscience.gdml.encodeToString

val gdml = Gdml{}
val gdmlString = gdml.encodeToString()
```
