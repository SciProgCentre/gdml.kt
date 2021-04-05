package space.kscience.gdml

import kotlin.reflect.typeOf


public interface GdmlRegistry: GdmlSolidRegistry, GdmlMaterialRegistry, GdmlDefineRegistry {
    public fun <R : GdmlGroup> registerGroup(item: R): GdmlRef<R>
}

public inline fun <reified R : GdmlNode> GdmlRegistry.generateName(suffix: String?): String =
    generateName(suffix, typeOf<R>())

