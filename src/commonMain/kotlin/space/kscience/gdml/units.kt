package space.kscience.gdml

import kotlinx.serialization.Serializable
import kotlin.math.PI

@Serializable(LUnitSerializer::class)
public enum class LUnit(public val title: String, public val value: Float) {
    MM("mm", 1f),
    CM("cm", 10f),
    M("m", 1000f)
}

@Serializable(AUnitSerializer::class)
public enum class AUnit(public val title: String, public val value: Float) {
    DEG("deg", PI.toFloat() / 180),
    DEGREE("deg", PI.toFloat() / 180),
    RAD("rad", 1f),
    RADIAN("rad", 1f)
}


public fun GdmlPosition.x(targetUnit: LUnit): Float = if (targetUnit == unit) {
    x.toFloat()
} else {
    x.toFloat() / targetUnit.value * (unit ?: LUnit.MM).value
}

public fun GdmlPosition.y(targetUnit: LUnit): Float = if (targetUnit == unit) {
    y.toFloat()
} else {
    y.toFloat() / targetUnit.value *  (unit ?: LUnit.MM).value
}

public fun GdmlPosition.z(targetUnit: LUnit): Float = if (targetUnit == this.unit) {
    z.toFloat()
} else {
    z.toFloat() / targetUnit.value *  (unit ?: LUnit.MM).value
}

public fun GdmlRotation.x(targetUnit: AUnit = AUnit.RAD): Float = if (targetUnit == this.unit) {
    x.toFloat()
} else {
    x.toFloat() / targetUnit.value *  (unit ?: AUnit.RAD).value
}

public fun GdmlRotation.y(targetUnit: AUnit = AUnit.RAD): Float = if (targetUnit == this.unit) {
    y.toFloat()
} else {
    y.toFloat() / targetUnit.value *  (unit ?: AUnit.RAD).value
}

public fun GdmlRotation.z(targetUnit: AUnit = AUnit.RAD): Float = if (targetUnit == this.unit) {
    z.toFloat()
} else {
    z.toFloat() / targetUnit.value *  (unit ?: AUnit.RAD).value
}

//public fun GdmlSolid.lscale(targetUnit: LUnit): Float {
//    val solidUnit = lunit ?: return 1f
//    return if (solidUnit == targetUnit) {
//        1f
//    } else {
//        solidUnit.value / targetUnit.value
//    }
//}
//
//public fun GdmlSolid.ascale(targetUnit: AUnit = AUnit.RAD): Float {
//    val solidUnit = aunit ?: return 1f
//    return if (solidUnit == targetUnit) {
//        1f
//    } else {
//        solidUnit.value / targetUnit.value
//    }
//}