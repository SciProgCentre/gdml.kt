package space.kscience.gdml.builder

import space.kscience.gdml.*
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@GdmlApi
public interface GdmlSolidRegistry{
    public fun <R : GdmlSolid> registerSolid(item: R): GdmlRef<R>
    public fun resolveName(providedName: String?, type: KType): String
}


public inline fun <reified T : GdmlNode> GdmlSolidRegistry.resolveName(providedName: String?): String =
    resolveName(providedName, typeOf<T>())

@GdmlApi
public inline fun GdmlSolidRegistry.box(
    x: Number,
    y: Number,
    z: Number,
    name: String? = null,
    block: GdmlBox.() -> Unit = {},
): GdmlRef<GdmlBox> = registerSolid(GdmlBox(resolveName<GdmlBox>(name), x, y, z).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.sphere(
    rmax: Number,
    name: String? = null,
    block: GdmlSphere.() -> Unit = {},
): GdmlRef<GdmlSphere> = registerSolid(GdmlSphere(resolveName<GdmlSphere>(name), rmax = rmax).apply(block))


@GdmlApi
public inline fun GdmlSolidRegistry.orb(
    r: Number,
    name: String? = null,
    block: GdmlOrb.() -> Unit = {},
): GdmlRef<GdmlOrb> = registerSolid(GdmlOrb(resolveName<GdmlOrb>(name), r).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.ellipsoid(
    ax: Number,
    by: Number,
    cz: Number,
    name: String? = null,
    block: GdmlEllipsoid.() -> Unit = {},
): GdmlRef<GdmlEllipsoid> = registerSolid(GdmlEllipsoid(resolveName<GdmlEllipsoid>(name), ax, by, cz).apply(block))


@GdmlApi
public inline fun GdmlSolidRegistry.eltube(
    dx: Number,
    dy: Number,
    dz: Number,
    name: String? = null,
    block: GdmlElTube.() -> Unit = {},
): GdmlRef<GdmlElTube> = registerSolid(GdmlElTube(resolveName<GdmlElTube>(name), dx, dy, dz).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.elcone(
    dx: Number,
    dy: Number,
    zmax: Number,
    zcut: Number,
    name: String? = null,
    block: GdmlElCone.() -> Unit = {},
): GdmlRef<GdmlElCone> = registerSolid(GdmlElCone(resolveName<GdmlElCone>(name), dx, dy, zmax, zcut).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.paraboloid(
    rlo: Number,
    rhi: Number,
    dz: Number,
    name: String? = null,
    block: GdmlParaboloid.() -> Unit = {},
): GdmlRef<GdmlParaboloid> = registerSolid(
    GdmlParaboloid(resolveName<GdmlParaboloid>(name), rlo, rhi, dz).apply(block)
)

@GdmlApi
public inline fun GdmlSolidRegistry.para(
    x: Number,
    y: Number,
    z: Number,
    name: String? = null,
    alpha: Number,
    theta: Number,
    phi: Number,
    block: GdmlParallelepiped.() -> Unit = {},
): GdmlRef<GdmlParallelepiped> = registerSolid(GdmlParallelepiped(
    resolveName<GdmlParallelepiped>(name),
    x, y, z, alpha, theta, phi
).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.torus(
    rmin: Number,
    rmax: Number,
    rtor: Number,
    name: String? = null,
    block: GdmlTorus.() -> Unit = {},
): GdmlRef<GdmlTorus> = registerSolid(GdmlTorus(resolveName<GdmlTorus>(name), rmin, rmax, rtor).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.trd(
    x1: Number,
    x2: Number,
    y1: Number,
    y2: Number,
    z: Number,
    name: String? = null,
    block: GdmlTrapezoid.() -> Unit = {},
): GdmlRef<GdmlTrapezoid> = registerSolid(
    GdmlTrapezoid(resolveName<GdmlTrapezoid>(name), x1, x2, y1, y2, z).apply(block)
)

@GdmlApi
public inline fun GdmlSolidRegistry.polyhedra(
    numsides: Int,
    name: String? = null,
    block: GdmlPolyhedra.() -> Unit = {},
): GdmlRef<GdmlPolyhedra> = registerSolid(GdmlPolyhedra(resolveName<GdmlPolyhedra>(name), numsides).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.polycone(
    name: String? = null,
    block: GdmlPolycone.() -> Unit = {},
): GdmlRef<GdmlPolycone> = registerSolid(GdmlPolycone(resolveName<GdmlPolycone>(name)).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.scaledSolid(
    solidref: GdmlRef<GdmlSolid>,
    scale: GdmlScale,
    name: String? = null,
    block: GdmlScaledSolid.() -> Unit = {},
): GdmlRef<GdmlScaledSolid> = registerSolid(
    GdmlScaledSolid(resolveName<GdmlScaledSolid>(name), solidref, scale).apply(block)
)

@GdmlApi
public inline fun GdmlSolidRegistry.tube(
    rmax: Number,
    z: Number,
    name: String,
    block: GdmlTube.() -> Unit = {},
): GdmlRef<GdmlTube> =
    registerSolid(GdmlTube(name, rmax, z).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.xtru(name: String, block: GdmlXtru.() -> Unit): GdmlRef<GdmlXtru> =
    registerSolid(GdmlXtru(name).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.cone(
    z: Number,
    rmax1: Number,
    rmax2: Number,
    name: String? = null,
    block: GdmlCone.() -> Unit = {},
): GdmlRef<GdmlCone> {
    val cone = GdmlCone(resolveName<GdmlCone>(name), z, rmax1, rmax2).apply(block)
    return registerSolid(cone)
}

@GdmlApi
public inline fun GdmlSolidRegistry.union(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    name: String? = null,
    block: GdmlUnion.() -> Unit = {},
): GdmlRef<GdmlUnion> {
    val union = GdmlUnion(resolveName<GdmlUnion>(name), first, second).apply(block)
    return registerSolid(union)
}

@GdmlApi
public inline fun GdmlSolidRegistry.intersection(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    name: String? = null,
    block: GdmlIntersection.() -> Unit = {},
): GdmlRef<GdmlIntersection> {
    val intersection = GdmlIntersection(resolveName<GdmlIntersection>(name), first, second).apply(block)
    return registerSolid(intersection)
}

@GdmlApi
public inline fun GdmlSolidRegistry.subtraction(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    name: String? = null,
    block: GdmlSubtraction.() -> Unit = {},
): GdmlRef<GdmlSubtraction> {
    val subtraction = GdmlSubtraction(resolveName<GdmlSubtraction>(name), first, second).apply(block)
    return registerSolid(subtraction)
}

