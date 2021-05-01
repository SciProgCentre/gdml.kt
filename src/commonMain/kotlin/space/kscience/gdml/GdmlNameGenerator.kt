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

internal fun String?.validateNCName(): String? {
    if(this != null){
        if(this.isBlank()) error("Empty names are not allowed")
        if(contains("[:@\$%&/+,;\\[\\](){} \t]".toRegex())){
            error("NCName '$this' could not contain any of [:@\$%&/+,;[](){}]")
        }
        if(get(0) in "0123456789+.") error("NCName '$this' could not start with [0123456789+.]")
    }
    return this
}