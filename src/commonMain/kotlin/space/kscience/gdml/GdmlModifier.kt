package space.kscience.gdml

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Compose-style properties for [GdmlPhysVolume]
 */
public class GdmlModifier(
    public val registry: GdmlRegistry,
    @PublishedApi internal val propertyMap: Map<KType, Any> = emptyMap(),
) {

    public inline fun <reified T : Any> get(): T? = propertyMap[typeOf<T>()] as? T

    public inline fun <reified T : Any> add(option: T): GdmlModifier =
        GdmlModifier(registry, propertyMap + (typeOf<T>() to option))

}

public fun GdmlModifier.position(x: Number = 0f, y: Number = 0f, z: Number = 0f, name: String? = null): GdmlModifier {
    val position = GdmlPosition(registry.generateName(name, typeOf<GdmlPosition>()), x, y, z)
    return add(position)
}

public fun GdmlModifier.position(ref: GdmlRef<GdmlPosition>): GdmlModifier{
    return add(ref)
}

public fun GdmlModifier.positionRef(
    x: Number = 0f,
    y: Number = 0f,
    z: Number = 0f,
    name: String? = null,
): GdmlModifier {
    val position = GdmlPosition(registry.generateName(name, typeOf<GdmlPosition>()), x, y, z)
    return add(registry.registerDefine(position))
}

public fun GdmlModifier.rotation(x: Number = 0f, y: Number = 0f, z: Number = 0f, name: String? = null): GdmlModifier {
    val rotation = GdmlRotation(registry.generateName(name, typeOf<GdmlPosition>()), x, y, z)
    return add(rotation)
}

public fun GdmlModifier.rotation(ref: GdmlRef<GdmlRotation>): GdmlModifier{
    return add(ref)
}

public fun GdmlModifier.rotationRef(
    x: Number = 0f,
    y: Number = 0f,
    z: Number = 0f,
    name: String? = null,
): GdmlModifier {
    val rotation = GdmlRotation(registry.generateName(name, typeOf<GdmlRotation>()), x, y, z)
    return add(registry.registerDefine(rotation))
}

public fun GdmlModifier.scale(x: Number = 1f, y: Number = 1f, z: Number = 1f, name: String? = null): GdmlModifier {
    val rotation = GdmlScale(registry.generateName(name, typeOf<GdmlPosition>()), x, y, z)
    return add(rotation)
}

public fun GdmlModifier.scale(ref: GdmlRef<GdmlScale>): GdmlModifier{
    return add(ref)
}

public fun GdmlModifier.scaleRef(
    x: Number = 1f,
    y: Number = 1f,
    z: Number = 1f,
    name: String? = null,
): GdmlModifier {
    val rotation = GdmlScale(registry.generateName(name, typeOf<GdmlScale>()), x, y, z)
    return add(registry.registerDefine(rotation))
}

public fun GdmlModifier.material(ref: GdmlRef<GdmlMaterial>): GdmlModifier = add(ref)