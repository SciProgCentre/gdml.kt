# Gdml bindings for Kotlin

[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/195530015.svg)](https://zenodo.org/badge/latestdoi/195530015)
[![Maven Central](https://img.shields.io/maven-central/v/space.kscience/gdml.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22space.kscience%22%20AND%20a:%22gdml%22)

[![Kotlin JS IR supported](https://img.shields.io/badge/Kotlin%2FJS-IR%20supported-yellow)](https://kotl.in/jsirsupported)

Multiplatform bindings for Gdml geometry specification. Utilized kotlinx.serialization
to read and writing Gdml configurations.

In the future, it will be possible to add additional module to launch GEANT4 simulations from Kotlin code.



> #### Artifact:
>
> This module artifact: `space.kscience:gdml:0.4.0-dev-7`.
>
>
> **Gradle:**
>
> ```groovy
> repositories {
>     mavenCentral()
>     maven { url "https://repo.kotlin.link" }
>     maven { url "https://dl.bintray.com/pdvrieze/maven" } // could be replaced by jcenter()
> //    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
> }
> 
> dependencies {
>     implementation 'space.kscience:gdml:0.4.0-dev-7'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     mavenCentral()
>     maven("https://repo.kotlin.link")
>     maven("https://dl.bintray.com/pdvrieze/maven") // could be replaced by jcenter()
> //    maven("https://dl.bintray.com/kotlin/kotlin-eap") // include for builds based on kotlin-eap
> }
> 
> dependencies {
>     implementation("space.kscience:gdml:0.4.0-dev-7")
> }
> ```

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
