package scientifik.gdml

import kotlin.math.PI

public enum class LUnit(public val title: String, public val value: Float) {
    MM("mm", 1f),
    CM("cm", 10f),
    M("m", 1000f)
}

public enum class AUnit(public val title: String, public val value: Float) {
    DEG("deg", PI.toFloat() / 180),
    DEGREE("deg", PI.toFloat() / 180),
    RAD("rad", 1f),
    RADIAN("rad", 1f)
}

public fun GDMLPosition.unit(): LUnit = LUnit.valueOf(unit.toUpperCase())

public fun GDMLPosition.x(unit: LUnit): Float = if (unit.name == this.unit) {
    x.toFloat()
} else {
    x.toFloat() / unit.value * unit().value
}

public fun GDMLPosition.y(unit: LUnit): Float = if (unit.name == this.unit) {
    y.toFloat()
} else {
    y.toFloat() / unit.value * unit().value
}

public fun GDMLPosition.z(unit: LUnit): Float = if (unit.name == this.unit) {
    z.toFloat()
} else {
    z.toFloat() / unit.value * unit().value
}

public fun GDMLRotation.unit(): AUnit = AUnit.valueOf(unit.toUpperCase())

public fun GDMLRotation.x(unit: AUnit = AUnit.RAD): Float = if (unit.name == this.unit) {
    x.toFloat()
} else {
    x.toFloat() / unit.value * unit().value
}

public fun GDMLRotation.y(unit: AUnit = AUnit.RAD): Float = if (unit.name == this.unit) {
    y.toFloat()
} else {
    y.toFloat() / unit.value * unit().value
}

public fun GDMLRotation.z(unit: AUnit = AUnit.RAD): Float = if (unit.name == this.unit) {
    z.toFloat()
} else {
    z.toFloat() / unit.value * unit().value
}

public fun GDMLSolid.lscale(unit: LUnit): Float {
    val solidUnit = lunit?.let { LUnit.valueOf(it.toUpperCase()) } ?: return 1f
    return if (solidUnit == unit) {
        1f
    } else {
        solidUnit.value / unit.value
    }
}

public fun GDMLSolid.ascale(unit: AUnit = AUnit.RAD): Float {
    val solidUnit = aunit?.let { AUnit.valueOf(it.toUpperCase()) } ?: return 1f
    return if (solidUnit == unit) {
        1f
    } else {
        solidUnit.value / unit.value
    }
}