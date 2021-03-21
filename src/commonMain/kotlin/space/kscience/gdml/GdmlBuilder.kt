//package space.kscience.gdml
//
//class GdmlBuilder(private val gdml: Gdml, private val prefix: String) {
//
//    @PublishedApi
//    internal fun <R : GdmlSolid> registerSolid(item: R): GdmlRef<R> = gdml.solids.register(item)
//
//    @PublishedApi
//    internal fun <R : GdmlGroup> registerGroup(item: R): GdmlRef<R> = gdml.structure.register(item)
//
//
//    @GdmlApi
//    public inline fun box(
//        x: Number,
//        y: Number,
//        z: Number,
//        name: String? = null,
//        block: GdmlBox.() -> Unit = {},
//    ): GdmlRef<GdmlBox> = registerSolid(GdmlBox(resolveName<GdmlBox>(name), x, y, z).apply(block))
//
//    @GdmlApi
//    public inline fun sphere(
//        rmax: Number,
//        name: String? = null,
//        block: GdmlSphere.() -> Unit = {},
//    ): GdmlRef<GdmlSphere> = register(GdmlSphere(resolveName<GdmlSphere>(name), rmax = rmax).apply(block))
//
//
//    @GdmlApi
//    public inline fun orb(
//        r: Number,
//        name: String? = null,
//        block: GdmlOrb.() -> Unit = {},
//    ): GdmlRef<GdmlOrb> = register(GdmlOrb(resolveName<GdmlOrb>(name), r).apply(block))
//
//    @GdmlApi
//    public inline fun ellipsoid(
//        ax: Number,
//        by: Number,
//        cz: Number,
//        name: String? = null,
//        block: GdmlEllipsoid.() -> Unit = {},
//    ): GdmlRef<GdmlEllipsoid> = register(GdmlEllipsoid(resolveName<GdmlEllipsoid>(name), ax, by, cz).apply(block))
//
//
//    @GdmlApi
//    public inline fun eltube(
//        dx: Number,
//        dy: Number,
//        dz: Number,
//        name: String? = null,
//        block: GdmlElTube.() -> Unit = {},
//    ): GdmlRef<GdmlElTube> = register(GdmlElTube(resolveName<GdmlElTube>(name), dx, dy, dz).apply(block))
//
//    @GdmlApi
//    public inline fun elcone(
//        dx: Number,
//        dy: Number,
//        zmax: Number,
//        zcut: Number,
//        name: String? = null,
//        block: GdmlElCone.() -> Unit = {},
//    ): GdmlRef<GdmlElCone> = register(GdmlElCone(resolveName<GdmlElCone>(name), dx, dy, zmax, zcut).apply(block))
//
//    @GdmlApi
//    public inline fun paraboloid(
//        rlo: Number,
//        rhi: Number,
//        dz: Number,
//        name: String? = null,
//        block: GdmlParaboloid.() -> Unit = {},
//    ): GdmlRef<GdmlParaboloid> = register(GdmlParaboloid(resolveName<GdmlParaboloid>(name), rlo, rhi, dz).apply(block))
//
//    @GdmlApi
//    public inline fun para(
//        x: Number,
//        y: Number,
//        z: Number,
//        name: String? = null,
//        alpha: Number,
//        theta: Number,
//        phi: Number,
//        block: GdmlParallelepiped.() -> Unit = {},
//    ): GdmlRef<GdmlParallelepiped> = register(GdmlParallelepiped(
//        resolveName<GdmlParallelepiped>(name),
//        x, y, z, alpha, theta, phi
//    ).apply(block))
//
//    @GdmlApi
//    public inline fun torus(
//        rmin: Number,
//        rmax: Number,
//        rtor: Number,
//        name: String? = null,
//        block: GdmlTorus.() -> Unit = {},
//    ): GdmlRef<GdmlTorus> = register(GdmlTorus(resolveName<GdmlTorus>(name), rmin, rmax, rtor).apply(block))
//
//    @GdmlApi
//    public inline fun trd(
//        x1: Number,
//        x2: Number,
//        y1: Number,
//        y2: Number,
//        z: Number,
//        name: String? = null,
//        block: GdmlTrapezoid.() -> Unit = {},
//    ): GdmlRef<GdmlTrapezoid> =
//        register(GdmlTrapezoid(resolveName<GdmlTrapezoid>(name), x1, x2, y1, y2, z).apply(block))
//
//    @GdmlApi
//    public inline fun polyhedra(
//        numsides: Int,
//        name: String? = null,
//        block: GdmlPolyhedra.() -> Unit = {},
//    ): GdmlRef<GdmlPolyhedra> = register(GdmlPolyhedra(resolveName<GdmlPolyhedra>(name), numsides).apply(block))
//
//    @GdmlApi
//    public inline fun polycone(
//        name: String? = null,
//        block: GdmlPolycone.() -> Unit = {},
//    ): GdmlRef<GdmlPolycone> = register(GdmlPolycone(resolveName<GdmlPolycone>(name)).apply(block))
//
//    @GdmlApi
//    public inline fun scaledSolid(
//        solidref: GdmlRef<GdmlSolid>,
//        scale: GdmlScale,
//        name: String? = null,
//        block: GdmlScaledSolid.() -> Unit = {},
//    ): GdmlRef<GdmlScaledSolid> =
//        register(GdmlScaledSolid(resolveName<GdmlScaledSolid>(name), solidref, scale).apply(block))
//
//    @GdmlApi
//    public inline fun tube(rmax: Number, z: Number, name: String, block: GdmlTube.() -> Unit = {}): GdmlRef<GdmlTube> =
//        register(GdmlTube(name, rmax, z).apply(block))
//
//    @GdmlApi
//    public inline fun xtru(name: String, block: GdmlXtru.() -> Unit): GdmlRef<GdmlXtru> =
//        register(GdmlXtru(name).apply(block))
//
//    @GdmlApi
//    public inline fun cone(
//        z: Number,
//        rmax1: Number,
//        rmax2: Number,
//        name: String? = null,
//        block: GdmlCone.() -> Unit = {},
//    ): GdmlRef<GdmlCone> {
//        val cone = GdmlCone(resolveName<GdmlCone>(name), z, rmax1, rmax2).apply(block)
//        return register(cone)
//    }
//
//    @GdmlApi
//    public inline fun union(
//        first: GdmlRef<GdmlSolid>,
//        second: GdmlRef<GdmlSolid>,
//        name: String? = null,
//        block: GdmlUnion.() -> Unit = {},
//    ): GdmlRef<GdmlUnion> {
//        val union = GdmlUnion(resolveName<GdmlUnion>(name), first, second).apply(block)
//        return register(union)
//    }
//
//    @GdmlApi
//    public inline fun intersection(
//        first: GdmlRef<GdmlSolid>,
//        second: GdmlRef<GdmlSolid>,
//        name: String? = null,
//        block: GdmlIntersection.() -> Unit = {},
//    ): GdmlRef<GdmlIntersection> {
//        val intersection = GdmlIntersection(resolveName<GdmlIntersection>(name), first, second).apply(block)
//        return register(intersection)
//    }
//
//    @GdmlApi
//    public inline fun subtraction(
//        first: GdmlRef<GdmlSolid>,
//        second: GdmlRef<GdmlSolid>,
//        name: String? = null,
//        block: GdmlSubtraction.() -> Unit = {},
//    ): GdmlRef<GdmlSubtraction> {
//        val subtraction = GdmlSubtraction(resolveName<GdmlSubtraction>(name), first, second).apply(block)
//        return register(subtraction)
//    }
//}