> #### Artifact:
>
> This module artifact: `${group}:${name}:${version}`.
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
>     implementation '${group}:${name}:${version}'
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
>     implementation("${group}:${name}:${version}")
> }
> ```