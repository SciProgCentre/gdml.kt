# GDML bindings for Kotlin

[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/195530015.svg)](https://zenodo.org/badge/latestdoi/195530015)
[![Maven Central](https://img.shields.io/maven-central/v/space.kscience/gdml.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22space.kscience%22%20AND%20a:%22gdml%22)
[![Kotlin JS IR supported](https://img.shields.io/badge/Kotlin%2FJS-IR%20supported-yellow)](https://kotl.in/jsirsupported)

**gdml.kt** is a Kotlin Multiplatform library providing type-safe bindings, an object model, and a declarative DSL for **[GDML (Geometry Description Markup Language)](https://gdml.web.cern.ch/GDML/)**. 

GDML is an XML-based geometry description format widely used in high-energy physics, Geant4 simulations, ROOT, and scientific detector modeling. **gdml.kt** makes it effortless to read, construct, manipulate, and export GDML geometry descriptions directly from Kotlin across JVM, JS, and Native targets.

---

## Capabilities

- **Kotlin Multiplatform**: Supports JVM, JavaScript (IR), and Kotlin/Native.
- **Type-Safe Geometry DSL**: Declarative and concise Kotlin builders for definitions, materials, solids, and volume hierarchies with automatic naming and reference management (`GdmlRef`).
- **Comprehensive GDML Schema Coverage**:
  - **Defines**: Positions, rotations, scales, constants, variables, quantities, and units (`mm`, `cm`, `m`, `rad`, `deg`).
  - **Materials**: Isotopes, elements, composite materials with fractional mass compositions, densities, states (`gas`, `liquid`, `solid`, `unknown`), and loading external materials from files or URLs.
  - **Solids (CSG & Primitives)**: `box`, `tube`, `sphere`, `orb`, `cone`, `torus`, `para`, `trd`, `trap`, `ellipsoid`, `eltube`, `elcone`, `paraboloid`, `polyhedra`, `polycone`, extruded solids (`xtru`), as well as boolean solids: `union`, `subtraction`, `intersection`, and `scaledSolid`.
  - **Structures & Placements**: Logical volumes (`volume`), assemblies (`assembly`), physical volume placements (`physVolume`) with local/referenced positions, rotations, scales, and volume divisions (`divisionVolume`).
- **Math Expressions & Preprocessor**: Evaluate arithmetic expressions and variable references embedded in GDML XML attributes via a built-in preprocessor.
- **Fast Multiplatform Serialization**: Powered by `kotlinx.serialization` and `xmlutil` for robust XML parsing and generation across all supported targets.
- **Kotlin Scripting & CLI (`gdml-script`)**: Author geometry setups using standalone Kotlin scripts (`.gdml.kts`) with implicit `Gdml` builders and compile them via CLI or programmatically.
- **Showcases & Geometry Utilities**: Built-in showcase models (e.g. `GdmlShowCase.cubes()`, `GdmlShowCase.babyIaxo()`) and utilities like `removeUnusedMaterials()`.

---

${features}

${artifact}

---

## Examples & Usage

### 1. Reading & Decoding GDML

GDML can be parsed from XML strings, input streams, files, or remote URLs.

```kotlin
import space.kscience.gdml.*
import java.io.File

// Decode from an XML string
val gdmlString: String = """<gdml> ... </gdml>"""
val gdml = Gdml.decodeFromString(gdmlString)

// Decode with mathematical expression evaluation enabled
val gdmlWithExpressions = Gdml.decodeFromString(gdmlString, usePreprocessor = true)

// (JVM) Decode from a File or InputStream
val fromFile = Gdml.decodeFromFile(File("detector.gdml"), usePreprocessor = true)

// (JVM) Fetch and decode directly from a remote URL
val fromUrl = Gdml.decodeFromUrl("https://example.com/geometries/detector.gdml")
```

---

### 2. Writing & Encoding GDML

Serialize a GDML model to an XML string, output stream, or file.

```kotlin
import space.kscience.gdml.*
import java.io.File

val gdml: Gdml = ...

// Serialize to a formatted XML string
val xmlString: String = gdml.encodeToString()

// (JVM) Write directly to a file or stream
gdml.encodeToFile(File("output.gdml"))
```

---

### 3. Constructing Geometry with the Kotlin DSL

Build entire GDML detector configurations using a type-safe declarative DSL.

```kotlin
import space.kscience.gdml.*
import kotlin.math.PI

val detectorGdml = Gdml {
    // 1. Defines: positions, rotations, etc.
    val centerPos = define.position(x = 0, y = 0, z = 0, name = "center")
    val offsetPos = define.position(z = 150.0, name = "offsetPos")

    // 2. Materials: isotopes, elements, composites
    val vacuum = materials.isotope("G4_Galactic")
    val copper = materials.element("Copper") {
        z = 29.0
        atom = GdmlAtom(63.546)
        density = GdmlDensity(8.96, "g/cm3")
    }

    // 3. Solids: 3D primitives
    val worldBox = solids.box(1000, 1000, 1000, "worldBox")
    val detectorTube = solids.tube(rmax = 50.0, z = 200.0, name = "detectorTube") {
        rmin = 20.0
        deltaphi = 360.0
        aunit = AUnit.DEG
    }

    // 4. Structure: volumes, assemblies, placements
    structure {
        val detectorVolume = volume(copper, detectorTube, "detectorVolume")

        world = volume(vacuum, worldBox, "world") {
            // Place detector volume inside the world
            physVolume(detectorVolume, "detectorPlacement") {
                positionref = offsetPos
                rotation {
                    x = 45.0
                    unit = AUnit.DEG
                }
            }
        }
    }
}
```

---

### 4. Boolean Solids (CSG)

Combine and cut primitive shapes using `subtraction`, `union`, and `intersection`.

```kotlin
import space.kscience.gdml.*

val gdml = Gdml {
    val outerBox = solids.box(100, 100, 100, "outerBox")
    val cutHole = solids.tube(rmax = 25, z = 120, name = "cutHole")

    // Subtract the cylinder from the box
    val hollowBox = solids.subtraction(outerBox, cutHole, "hollowBox") {
        position(x = 0, y = 0, z = 0)
    }
}
```

---

### 5. Extruded Solids (`xtru`) and Polyhedra

Define complex 2D cross-sections and extrude them along the Z-axis.

```kotlin
import space.kscience.gdml.*

val gdml = Gdml {
    val extruded = solids.xtru("extrudedSolid") {
        // Define 2D polygon vertices
        vertex(x = -50.0, y = -50.0)
        vertex(x =  50.0, y = -50.0)
        vertex(x =  50.0, y =  50.0)
        vertex(x = -50.0, y =  50.0)

        // Define Z sections
        section(index = 0, z = -100.0) {
            scalingFactor = 1.0
        }
        section(index = 1, z = 100.0) {
            scalingFactor = 1.5
            xOffset = 10.0
        }
    }
}
```

---

### 6. Group Builder & Assemblies

Create modular assemblies and reuse them in complex detector arrays.

```kotlin
import space.kscience.gdml.*
import kotlin.math.PI

val gdml = Gdml {
    val cube = solids.box(40, 40, 40)
    val alu = materials.element("Aluminium")

    // Group items using the builder
    val layer = buildGroup("layer") {
        repeat(5) { i ->
            solid(cube, modifier = position(x = i * 50).material(alu))
        }
    }

    world = buildGroup("world") {
        group(layer, modifier = rotation(z = PI / 4))
        group(layer, modifier = rotation(z = -PI / 4))
    }
}
```

---

### 7. Kotlin Scripting (`.gdml.kts`) & CLI

With the `gdml-script` module, you can write standalone geometry scripts using `.gdml.kts` files.

#### Example Script (`detector.gdml.kts`):

```kotlin
// Inside detector.gdml.kts - top-level receiver is Gdml builder
structure {
    val worldBox = solids.box(500, 500, 500)
    val air = materials.isotope("G4_AIR")
    val copper = materials.element("Copper")

    val tube = solids.tube(rmax = 30, z = 100)
    val tubeVolume = volume(copper, tube)

    world = volume(air, worldBox) {
        for (i in 0 until 4) {
            physVolume(tubeVolume) {
                position(x = i * 60, y = 0, z = 0)
            }
        }
    }
}
```

#### Running via CLI:

```bash
# Render .gdml.kts script directly to a GDML XML file
gdml-script detector.gdml.kts output.gdml

# Or output to standard output
gdml-script detector.gdml.kts
```

#### Evaluating Programmatically:

```kotlin
import space.kscience.gdml.script.Gdml
import space.kscience.gdml.encodeToString
import kotlin.script.experimental.host.toScriptSource
import java.io.File

val scriptFile = File("detector.gdml.kts")
val gdml = Gdml(scriptFile.toScriptSource())
println(gdml.encodeToString())
```
