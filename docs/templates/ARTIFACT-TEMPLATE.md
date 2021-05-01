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
> }
> 
> dependencies {
>     implementation("${group}:${name}:${version}")
> }
> ```