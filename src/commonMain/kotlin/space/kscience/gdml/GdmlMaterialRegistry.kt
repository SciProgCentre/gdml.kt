package space.kscience.gdml

import kotlin.reflect.typeOf


public interface GdmlMaterialRegistry : GdmlNameGenerator {
    public fun <R : GdmlMaterial> registerMaterial(item: R): GdmlRef<R>

    public val defaultMaterial: GdmlRef<GdmlMaterial>
}

public inline fun GdmlMaterialRegistry.isotope(name: String? = null, build: GdmlIsotope.() -> Unit = {}): GdmlRef<GdmlIsotope> =
    registerMaterial(GdmlIsotope(generateName(name, typeOf<GdmlIsotope>())).apply(build))

public inline fun GdmlMaterialRegistry.element(name: String? = null, build: GdmlElement.() -> Unit = {}): GdmlRef<GdmlElement> =
    registerMaterial(GdmlElement(generateName(name,  typeOf<GdmlElement>())).apply(build))

public inline fun GdmlMaterialRegistry.composite(name: String? = null, build: GdmlComposite.() -> Unit = {}): GdmlRef<GdmlComposite> =
    registerMaterial(GdmlComposite(generateName(name,  typeOf<GdmlComposite>())).apply(build))
