package space.kscience.gdml

public interface GdmlSolidRegistry : GdmlNameGenerator {
    public fun <R : GdmlSolid> registerSolid(item: R): GdmlRef<R>
}


@GdmlApi
public inline fun GdmlSolidRegistry.box(
    x: Number,
    y: Number,
    z: Number,
    name: String? = null,
    block: GdmlBox.() -> Unit = {},
): GdmlRef<GdmlBox> = registerSolid(GdmlBox(generateName<GdmlBox>(name), x, y, z).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.sphere(
    rmax: Number,
    name: String? = null,
    block: GdmlSphere.() -> Unit = {},
): GdmlRef<GdmlSphere> = registerSolid(GdmlSphere(generateName<GdmlSphere>(name), rmax = rmax).apply(block))


@GdmlApi
public inline fun GdmlSolidRegistry.orb(
    r: Number,
    name: String? = null,
    block: GdmlOrb.() -> Unit = {},
): GdmlRef<GdmlOrb> = registerSolid(GdmlOrb(generateName<GdmlOrb>(name), r).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.ellipsoid(
    ax: Number,
    by: Number,
    cz: Number,
    name: String? = null,
    block: GdmlEllipsoid.() -> Unit = {},
): GdmlRef<GdmlEllipsoid> = registerSolid(GdmlEllipsoid(generateName<GdmlEllipsoid>(name), ax, by, cz).apply(block))


@GdmlApi
public inline fun GdmlSolidRegistry.eltube(
    dx: Number,
    dy: Number,
    dz: Number,
    name: String? = null,
    block: GdmlElTube.() -> Unit = {},
): GdmlRef<GdmlElTube> = registerSolid(GdmlElTube(generateName<GdmlElTube>(name), dx, dy, dz).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.elcone(
    dx: Number,
    dy: Number,
    zmax: Number,
    zcut: Number,
    name: String? = null,
    block: GdmlElCone.() -> Unit = {},
): GdmlRef<GdmlElCone> = registerSolid(GdmlElCone(generateName<GdmlElCone>(name), dx, dy, zmax, zcut).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.paraboloid(
    rlo: Number,
    rhi: Number,
    dz: Number,
    name: String? = null,
    block: GdmlParaboloid.() -> Unit = {},
): GdmlRef<GdmlParaboloid> = registerSolid(
    GdmlParaboloid(generateName<GdmlParaboloid>(name), rlo, rhi, dz).apply(block)
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
    generateName<GdmlParallelepiped>(name),
    x, y, z, alpha, theta, phi
).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.torus(
    rmin: Number,
    rmax: Number,
    rtor: Number,
    name: String? = null,
    block: GdmlTorus.() -> Unit = {},
): GdmlRef<GdmlTorus> = registerSolid(GdmlTorus(generateName<GdmlTorus>(name), rmin, rmax, rtor).apply(block))

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
    GdmlTrapezoid(generateName<GdmlTrapezoid>(name), x1, x2, y1, y2, z).apply(block)
)

@GdmlApi
public inline fun GdmlSolidRegistry.polyhedra(
    numsides: Int,
    name: String? = null,
    block: GdmlPolyhedra.() -> Unit = {},
): GdmlRef<GdmlPolyhedra> = registerSolid(GdmlPolyhedra(generateName<GdmlPolyhedra>(name), numsides).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.polycone(
    name: String? = null,
    block: GdmlPolycone.() -> Unit = {},
): GdmlRef<GdmlPolycone> = registerSolid(GdmlPolycone(generateName<GdmlPolycone>(name)).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.scaledSolid(
    solidref: GdmlRef<GdmlSolid>,
    scale: GdmlScale,
    name: String? = null,
    block: GdmlScaledSolid.() -> Unit = {},
): GdmlRef<GdmlScaledSolid> = registerSolid(
    GdmlScaledSolid(generateName<GdmlScaledSolid>(name), solidref, scale).apply(block)
)

@GdmlApi
public inline fun GdmlSolidRegistry.tube(
    rmax: Number,
    z: Number,
    name: String? = null,
    block: GdmlTube.() -> Unit = {},
): GdmlRef<GdmlTube> =
    registerSolid(GdmlTube(generateName<GdmlTube>(name), rmax, z).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.xtru(
    name: String? = null,
    block: GdmlXtru.() -> Unit,
): GdmlRef<GdmlXtru> =
    registerSolid(GdmlXtru(generateName<GdmlXtru>(name)).apply(block))

@GdmlApi
public inline fun GdmlSolidRegistry.cone(
    z: Number,
    rmax1: Number,
    rmax2: Number,
    name: String? = null,
    block: GdmlCone.() -> Unit = {},
): GdmlRef<GdmlCone> {
    val cone = GdmlCone(generateName<GdmlCone>(name), z, rmax1, rmax2).apply(block)
    return registerSolid(cone)
}

@GdmlApi
public inline fun GdmlSolidRegistry.union(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    name: String? = null,
    block: GdmlUnion.() -> Unit = {},
): GdmlRef<GdmlUnion> {
    val union = GdmlUnion(generateName<GdmlUnion>(name), first, second).apply(block)
    return registerSolid(union)
}

@GdmlApi
public inline fun GdmlSolidRegistry.intersection(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    name: String? = null,
    block: GdmlIntersection.() -> Unit = {},
): GdmlRef<GdmlIntersection> {
    val intersection = GdmlIntersection(generateName<GdmlIntersection>(name), first, second).apply(block)
    return registerSolid(intersection)
}

@GdmlApi
public inline fun GdmlSolidRegistry.subtraction(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    name: String? = null,
    block: GdmlSubtraction.() -> Unit = {},
): GdmlRef<GdmlSubtraction> {
    val subtraction = GdmlSubtraction(generateName<GdmlSubtraction>(name), first, second).apply(block)
    return registerSolid(subtraction)
}

