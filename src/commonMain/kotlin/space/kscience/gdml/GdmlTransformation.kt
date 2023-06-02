package space.kscience.gdml

/**
 * A general [Gdml] transformation
 */
public fun interface GdmlTransformation {
    public fun transform(gdml: Gdml): Gdml
}

/**
 * Inject given units in all [GdmlSolid], [GdmlPosition] and [GdmlRotation] if they are not defined previously
 */
public class InjectUnits(public val lUnit: LUnit?, public val aUnit: AUnit?) : GdmlTransformation {
    override fun transform(gdml: Gdml): Gdml {
        if (lUnit == null && aUnit == null) return gdml // fast return if no transformations are present
        gdml.containers.filterIsInstance<GdmlDefineContainer>().flatMap { it.content }.forEach {
            when (it) {
                is GdmlPosition -> if (it.unit == null) it.unit = lUnit
                is GdmlRotation -> if (it.unit == null) it.unit = aUnit

                else -> { /*do nothing*/ }
            }
        }
        gdml.containers.filterIsInstance<GdmlSolidContainer>().flatMap { it.content }.forEach { solid ->
            //Changing all solids
            if (solid.lunit == null) {
                solid.lunit = lUnit
            }
            if (solid.aunit == null) {
                solid.aunit = aUnit
            }
        }
        return gdml
    }
}

/**
 * Apply one or several **in-place** transformations to a [Gdml] and return the result.
 */
public fun Gdml.transform(vararg transformation: GdmlTransformation): Gdml {
    var gdml = this
    transformation.forEach {
        gdml = it.transform(gdml)
    }
    return gdml
}

public fun Gdml.withUnits(lUnit: LUnit, aUnit: AUnit): Gdml = transform(InjectUnits(lUnit, aUnit))