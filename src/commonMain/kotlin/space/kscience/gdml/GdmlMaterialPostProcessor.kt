package space.kscience.gdml

public class GdmlMaterialPostProcessor {
    public companion object {
        public fun removeUnusedMaterials(gdml: Gdml): Gdml {

            val materialsInUse: MutableSet<GdmlMaterial> = mutableSetOf()
            val elementsInUse: MutableSet<GdmlElement> = mutableSetOf()
            val isotopesInUse: MutableSet<GdmlIsotope> = mutableSetOf()

            fun addMaterialsRecursively(composite: GdmlComposite?) {
                // composite may be composed of other composites
                composite?.fractions?.forEach { fraction ->
                    val materialComponent = gdml.materials.get<GdmlComposite>(fraction.ref)
                    if (materialComponent != null) {
                        materialsInUse.add(materialComponent)
                        addMaterialsRecursively(materialComponent)
                    } else {
                        val elementComponent = gdml.materials.get<GdmlElement>(fraction.ref)
                        if (elementComponent != null) {
                            elementsInUse.add(elementComponent)
                            elementComponent.fractions.forEach { elementFraction ->
                                val isotopeComponent = gdml.materials.get<GdmlIsotope>(elementFraction.ref)
                                isotopesInUse.add(isotopeComponent!!) // element must be mode of isotopes
                            }
                        } else {
                            val isotopeComponent = gdml.materials.get<GdmlIsotope>(fraction.ref)
                            if (isotopeComponent != null) {
                                isotopesInUse.add(isotopeComponent)
                            }
                        }
                    }
                }
            }

            gdml.structure.content.forEach {
                val volume = gdml.structure.get<GdmlVolume>(it.name)
                if (volume != null) {
                    val material = volume.materialref.resolve(gdml)!!
                    materialsInUse.add(material)
                    val composite = gdml.materials.get<GdmlComposite>(material.name)
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

            gdml.materials.content.clear()
            // Refill materials only with used ones
            isotopesInUse.forEach {
                gdml.materials.content.add(it)
            }
            elementsInUse.forEach {
                gdml.materials.content.add(it)
            }
            materialsInUse.forEach {
                gdml.materials.content.add(it)
            }

            return gdml
        }
    }
}