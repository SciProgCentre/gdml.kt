> #### Artifact:
>
> This module artifact: `${group}:${name}:${version}`.
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
>     implementation '${group}:${name}:${version}'
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
>     implementation("${group}:${name}:${version}")
> }
> ```