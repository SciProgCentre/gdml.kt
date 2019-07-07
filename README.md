# GDML bindings for Kotlin
Multiplatform bindings for GDML geometry specification. Utilized kotlinx.serialization
to read (**does not work yet**) and writing GDML configurations.

In future it is possible to add additional module to launch GEANT4 simulations from Kotlin code.

The artifacts could be accessed via following configurations: 
```kotlin
repository{
    maven("http://npm.mipt.ru:8081/artifactory/gradle-dev-local/")
}

//for multiplatform with gradle-metadata:
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("scientifik:gdml:0.1.0")
            }
        }
    }
}

//for jvm
dependencies {
    api("scientifik:gdml-jvm:0.1.0")
}

```
