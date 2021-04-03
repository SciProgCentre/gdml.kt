package space.kscience.gdml.builder

import space.kscience.gdml.*
import kotlin.reflect.typeOf

@GdmlApi
public interface GdmlRegistry: GdmlSolidRegistry {
    public fun <R : GdmlDefine> registerDefine(item: R): GdmlRef<R>
    public fun <R : GdmlMaterial> registerMaterial(item: R): GdmlRef<R>
    public fun <R : GdmlGroup> registerGroup(item: R): GdmlRef<R>

    public val defaultMaterial: GdmlRef<GdmlMaterial>
}

public inline fun <reified R : GdmlNode> GdmlRegistry.resolveName(suffix: String?): String =
    resolveName(suffix, typeOf<R>())

