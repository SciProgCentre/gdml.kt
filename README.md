# Gdml bindings for Kotlin

[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/195530015.svg)](https://zenodo.org/badge/latestdoi/195530015)
[ ![Download](https://api.bintray.com/packages/mipt-npm/scientifik/gdml/images/download.svg) ](https://bintray.com/mipt-npm/scientifik/gdml/_latestVersion)

[![Kotlin JS IR supported](https://img.shields.io/badge/Kotlin%2FJS-IR%20supported-yellow)](https://kotl.in/jsirsupported)

Multiplatform bindings for Gdml geometry specification. Utilized kotlinx.serialization
to read and writing Gdml configurations.

In the future, it will be possible to add additional module to launch GEANT4 simulations from Kotlin code.



> #### Artifact:
>
> This module artifact: `space.kscience:gdml:0.3.0`.
>
>
> **Gradle:**
>
> ```groovy
> repositories {
>     maven { url 'https://repo.kotlin.link' }
> //    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
> }
> 
> dependencies {
>     implementation 'space.kscience:gdml:0.3.0'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://repo.kotlin.link")
> //    maven("https://dl.bintray.com/kotlin/kotlin-eap") // include for builds based on kotlin-eap
> }
> 
> dependencies {
>     implementation("space.kscience:gdml:0.3.0")
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
