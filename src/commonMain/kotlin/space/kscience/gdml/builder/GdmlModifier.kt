package space.kscience.gdml.builder

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Compose-style properties for [GdmlPhysVolume]
 */
public class GdmlModifier(@PublishedApi internal val propertyMap: Map<KType, Any>) {

    public inline fun <reified T : Any> get(): T? = propertyMap[typeOf<T>()] as? T

    public inline fun <reified T : Any> add(option: T): GdmlModifier =
        GdmlModifier(propertyMap + (typeOf<T>() to option))

    public companion object{
        public val EMPTY: GdmlModifier = GdmlModifier(emptyMap())
    }
}