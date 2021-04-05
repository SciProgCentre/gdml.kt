package space.kscience.gdml

import kotlin.reflect.KType
import kotlin.reflect.typeOf


/**
 * A builder for GDML structure groups
 */
@GdmlApi
public class GdmlBuilder(public val registry: GdmlRegistry, public val parent: GdmlGroup) {

    public fun physVolume(
        modifier: GdmlModifier,
        namePrefix: String?,
        volumeRef: GdmlRef<GdmlGroup>,
        type: KType,
    ): GdmlPhysVolume =
        parent.physVolume(volumeRef, registry.generateName(namePrefix?.let { "$namePrefix-physVol" }, type)) {
            modifier.get<GdmlPosition>()?.let { position = it }
            modifier.get<GdmlRef<GdmlPosition>>()?.let { positionref = it }
            modifier.get<GdmlRotation>()?.let { rotation = it }
            modifier.get<GdmlRef<GdmlRotation>>()?.let { rotationref = it }
            modifier.get<GdmlScale>()?.let { scale = it }
            modifier.get<GdmlRef<GdmlScale>>()?.let { scaleref = it }
        }


    /**
     * Create an empty modifier
     */
    public fun modifier(): GdmlModifier = GdmlModifier(registry)

    /**
     * Create a modifier with position
     */
    public fun position(x: Number = 0f, y: Number = 0f, z: Number = 0f, name: String? = null): GdmlModifier =
        modifier().position(x, y, z, name)

    /**
     * Create a modified with position ref
     */
    public fun position(ref: GdmlRef<GdmlPosition>): GdmlModifier = modifier().position(ref)

    /**
     * Create a modifier with rotation
     */
    public fun rotation(x: Number = 0f, y: Number = 0f, z: Number = 0f, name: String? = null): GdmlModifier =
        modifier().rotation(x, y, z, name)

    /**
     * Create a modified with rotation ref
     */
    public fun rotation(ref: GdmlRef<GdmlRotation>): GdmlModifier = modifier().rotation(ref)

    /**
     * Create a modifier with scale
     */
    public fun scale(x: Number = 1f, y: Number = 1f, z: Number = 1f, name: String? = null): GdmlModifier =
        modifier().scale(x, y, z, name)

    /**
     * Create a modified with rotation ref
     */
    public fun scale(ref: GdmlRef<GdmlScale>): GdmlModifier = modifier().scale(ref)

    @GdmlApi
    public inline fun <reified T : GdmlSolid> solid(
        modifier: GdmlModifier,
        namePrefix: String?,
        builder: (String) -> T,
    ): GdmlPhysVolume {
        val type = typeOf<T>()
        val name = registry.generateName(namePrefix?.let { "$namePrefix-solid" }, type)
        val solid = builder(name)
        val solidRef = registry.registerSolid(solid)
        val volume: GdmlVolume = GdmlVolume(
            registry.generateName(namePrefix?.let { "$namePrefix-volume" }, type),
            modifier.get() ?: registry.defaultMaterial,
            solidRef
        )
        val volumeRef = registry.registerGroup(volume)
        return physVolume(modifier, namePrefix, volumeRef, type)
    }


    @GdmlApi
    public inline fun group(
        modifier: GdmlModifier = modifier(),
        namePrefix: String? = null,
        @GdmlApi builder: GdmlBuilder.() -> Unit,
    ): GdmlPhysVolume {
        val assembly =
            GdmlAssembly(registry.generateName(namePrefix?.let { "$namePrefix-group" }, typeOf<GdmlAssembly>()))
        GdmlBuilder(registry, assembly).apply(builder)
        val assemblyRef = registry.registerGroup(assembly)
        return physVolume(modifier, namePrefix, assemblyRef, typeOf<GdmlAssembly>())
    }


}

@GdmlApi
public fun Gdml.group(name: String = "world", @GdmlApi builder: GdmlBuilder.() -> Unit): GdmlRef<GdmlGroup> {
    val assembly = GdmlAssembly(name)
    GdmlBuilder(this, assembly).apply(builder)
    return registerGroup(assembly)
}

@GdmlApi
public inline fun GdmlBuilder.box(
    x: Number,
    y: Number,
    z: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlBox.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlBox(it, x, y, z).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.sphere(
    rmax: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlSphere.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlSphere(it, rmax = rmax).apply(block) }


@GdmlApi
public inline fun GdmlBuilder.orb(
    r: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlOrb.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlOrb(it, r).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.ellipsoid(
    ax: Number,
    by: Number,
    cz: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlEllipsoid.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlEllipsoid(it, ax, by, cz).apply(block) }


@GdmlApi
public inline fun GdmlBuilder.eltube(
    dx: Number,
    dy: Number,
    dz: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlElTube.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlElTube(it, dx, dy, dz).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.elcone(
    dx: Number,
    dy: Number,
    zmax: Number,
    zcut: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlElCone.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlElCone(it, dx, dy, zmax, zcut).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.paraboloid(
    rlo: Number,
    rhi: Number,
    dz: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlParaboloid.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) {
    GdmlParaboloid(it, rlo, rhi, dz).apply(block)
}

@GdmlApi
public inline fun GdmlBuilder.para(
    x: Number,
    y: Number,
    z: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    alpha: Number,
    theta: Number,
    phi: Number,
    block: GdmlParallelepiped.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) {
    GdmlParallelepiped(it, x, y, z, alpha, theta, phi).apply(block)
}

@GdmlApi
public inline fun GdmlBuilder.torus(
    rmin: Number,
    rmax: Number,
    rtor: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlTorus.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlTorus(it, rmin, rmax, rtor).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.trd(
    x1: Number,
    x2: Number,
    y1: Number,
    y2: Number,
    z: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlTrapezoid.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) {
    GdmlTrapezoid(it, x1, x2, y1, y2, z).apply(block)
}

@GdmlApi
public inline fun GdmlBuilder.polyhedra(
    numsides: Int,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlPolyhedra.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlPolyhedra(it, numsides).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.polycone(
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlPolycone.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlPolycone(it).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.scaledSolid(
    solidref: GdmlRef<GdmlSolid>,
    scale: GdmlScale,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlScaledSolid.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) {
    GdmlScaledSolid(it, solidref, scale).apply(block)
}

@GdmlApi
public inline fun GdmlBuilder.tube(
    rmax: Number,
    z: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlTube.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { (GdmlTube(it, rmax, z).apply(block)) }

@GdmlApi
public inline fun GdmlBuilder.xtru(
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlXtru.() -> Unit,
): GdmlPhysVolume = solid(modifier, name) { (GdmlXtru(it).apply(block)) }

@GdmlApi
public inline fun GdmlBuilder.cone(
    z: Number,
    rmax1: Number,
    rmax2: Number,
    modifier: GdmlModifier = modifier(),
    name: String? = null,
    block: GdmlCone.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlCone(it, z, rmax1, rmax2).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.union(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    modifier: GdmlModifier = modifier(), name: String? = null,
    block: GdmlUnion.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlUnion(it, first, second).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.intersection(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    modifier: GdmlModifier = modifier(), name: String? = null,
    block: GdmlIntersection.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlIntersection(it, first, second).apply(block) }

@GdmlApi
public inline fun GdmlBuilder.subtraction(
    first: GdmlRef<GdmlSolid>,
    second: GdmlRef<GdmlSolid>,
    modifier: GdmlModifier = modifier(), name: String? = null,
    block: GdmlSubtraction.() -> Unit = {},
): GdmlPhysVolume = solid(modifier, name) { GdmlSubtraction(it, first, second).apply(block) }
