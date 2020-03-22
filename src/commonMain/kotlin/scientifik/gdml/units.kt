package scientifik.gdml

import kotlin.math.PI

enum class LUnit(val title: String, val value: Float) {
    MM("mm", 1f),
    CM("cm", 10f),
    M("m", 1000f)
}

enum class AUnit(val title: String,val value: Float) {
    DEG("deg", PI.toFloat() / 180),
    DEGREE("deg", PI.toFloat() / 180),
    RAD("rad", 1f),
    RADIAN("rad", 1f)
}

fun GDMLPosition.unit(): LUnit = LUnit.valueOf(unit.toUpperCase())

fun GDMLPosition.x(unit: LUnit): Float = if (unit.name == this.unit) {
    x.toFloat()
} else {
    x.toFloat() / unit.value * unit().value
}

fun GDMLPosition.y(unit: LUnit): Float = if (unit.name == this.unit) {
    y.toFloat()
} else {
    y.toFloat() / unit.value * unit().value
}

fun GDMLPosition.z(unit: LUnit): Float = if (unit.name == this.unit) {
    z.toFloat()
} else {
    z.toFloat() / unit.value * unit().value
}

fun GDMLRotation.unit(): AUnit = AUnit.valueOf(unit.toUpperCase())

fun GDMLRotation.x(unit: AUnit = AUnit.RAD): Float = if (unit.name == this.unit) {
    x.toFloat()
} else {
    x.toFloat() / unit.value * unit().value
}

fun GDMLRotation.y(unit: AUnit = AUnit.RAD): Float = if (unit.name == this.unit) {
    y.toFloat()
} else {
    y.toFloat() / unit.value * unit().value
}

fun GDMLRotation.z(unit: AUnit = AUnit.RAD): Float = if (unit.name == this.unit) {
    z.toFloat()
} else {
    z.toFloat() / unit.value * unit().value
}

fun GDMLSolid.lscale(unit: LUnit): Float {
    val solidUnit = lunit?.let { LUnit.valueOf(it.toUpperCase()) } ?: return 1f
    return if (solidUnit == unit) {
        1f
    } else {
        solidUnit.value / unit.value
    }
}

fun GDMLSolid.ascale(unit: AUnit = AUnit.RAD): Float {
    val solidUnit = aunit?.let { AUnit.valueOf(it.toUpperCase()) } ?: return 1f
    return if (solidUnit == unit) {
        1f
    } else {
        solidUnit.value / unit.value
    }
}