package space.kscience.gdml

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * An interface used for a container which allows to automatically generate name for a GDML entry
 */
public interface GdmlNameGenerator {
    public fun generateName(providedName: String?, type: KType): String
}

public inline fun <reified T : GdmlNode> GdmlNameGenerator.generateName(providedName: String?): String =
    generateName(providedName, typeOf<T>())