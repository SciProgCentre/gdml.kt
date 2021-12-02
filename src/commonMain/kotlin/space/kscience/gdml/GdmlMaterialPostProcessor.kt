package space.kscience.gdml

public fun Gdml.removeUnusedMaterials(): Gdml {

    val materialsInUse: MutableSet<GdmlMaterial> = mutableSetOf()
    val elementsInUse: MutableSet<GdmlElement> = mutableSetOf()
    val isotopesInUse: MutableSet<GdmlIsotope> = mutableSetOf()

    fun addMaterialsRecursively(composite: GdmlComposite?) {
        // composite may be composed of other composites
        composite?.fractions?.forEach { fraction ->
            val materialComponent = this.materials.get<GdmlComposite>(fraction.ref)
            if (materialComponent != null) {
                materialsInUse.add(materialComponent)
                addMaterialsRecursively(materialComponent)
            } else {
                val elementComponent = this.materials.get<GdmlElement>(fraction.ref)
                if (elementComponent != null) {
                    elementsInUse.add(elementComponent)
                    elementComponent.fractions.forEach { elementFraction ->
                        val isotopeComponent = this.materials.get<GdmlIsotope>(elementFraction.ref)
                        isotopesInUse.add(isotopeComponent!!) // element must be mode of isotopes
                    }
                } else {
                    val isotopeComponent = this.materials.get<GdmlIsotope>(fraction.ref)
                    if (isotopeComponent != null) {
                        isotopesInUse.add(isotopeComponent)
                    }
                }
            }
        }
    }

    this.structure.content.forEach {
        val volume = this.structure.get<GdmlVolume>(it.name)
        if (volume != null) {
            val material = volume.materialref.resolve(this)!!
            materialsInUse.add(material)
            val composite = this.materials.get<GdmlComposite>(material.name)
            addMaterialsRecursively(composite)
        }
    }


    // materialsInUse now contains all materials in use
    /*
    println(" - ISOTOPES:")
    isotopesInUse.forEach { println(it) }
    println(" - ELEMENTS:")
    elementsInUse.forEach { println(it) }
    println(" - MATERIALS:")
    materialsInUse.forEach { println(it) }
    */

    this.materials.content.clear()
    // Refill materials only with used ones
    isotopesInUse.forEach {
        this.materials.content.add(it)
    }
    elementsInUse.forEach {
        this.materials.content.add(it)
    }
    materialsInUse.forEach {
        this.materials.content.add(it)
    }

    return this
}
