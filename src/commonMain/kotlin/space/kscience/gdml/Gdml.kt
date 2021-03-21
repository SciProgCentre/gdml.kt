@file:UseSerializers(NumberSerializer::class)
@file:Suppress("unused")

package space.kscience.gdml

import kotlinx.serialization.*
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@DslMarker
public annotation class GdmlApi

@Serializable
@SerialName("gdml")
public class Gdml {

    public val containers: ArrayList<GdmlContainer<*>> = arrayListOf(
        GdmlDefineContainer(),
        GdmlMaterialContainer(),
        GdmlSolidContainer(),
        GdmlStructure()
    )

    public val setup: GdmlSetup = GdmlSetup()

    public val define: GdmlDefineContainer
        get() = (containers.filterIsInstance<GdmlDefineContainer>().firstOrNull()
            ?: GdmlDefineContainer().also { containers.add(0, it) })
    public val materials: GdmlMaterialContainer
        get() = (containers.filterIsInstance<GdmlMaterialContainer>().firstOrNull()
            ?: GdmlMaterialContainer().also { containers.add(it) })
    public val solids: GdmlSolidContainer
        get() = (containers.filterIsInstance<GdmlSolidContainer>().firstOrNull()
            ?: GdmlSolidContainer().also { containers.add(it) })
    public val structure: GdmlStructure
        get() = (containers.filterIsInstance<GdmlStructure>().firstOrNull()
            ?: GdmlStructure().also { containers.add(it) })

    @GdmlApi
    public inline fun define(block: GdmlDefineContainer.() -> Unit) {
        define.apply(block)
    }

    @GdmlApi
    public inline fun materials(block: GdmlMaterialContainer.() -> Unit) {
        materials.apply(block)
    }

    @GdmlApi
    public inline fun solids(block: GdmlSolidContainer.() -> Unit) {
        solids.apply(block)
    }

    @GdmlApi
    public inline fun structure(block: GdmlStructure.() -> Unit) {
        structure.apply(block)
    }

    public inline fun <reified T : GdmlDefine> getDefine(ref: String): T? =
        containers.filterIsInstance<GdmlDefineContainer>().mapNotNull { it.get<T>(ref) }.singleOrNull()

    public inline fun <reified T : GdmlMaterial> getMaterial(ref: String): T? =
        containers.filterIsInstance<GdmlMaterialContainer>().mapNotNull { it.get<T>(ref) }.singleOrNull()

    public inline fun <reified T : GdmlSolid> getSolid(ref: String): T? =
        containers.filterIsInstance<GdmlSolidContainer>().mapNotNull { it.get<T>(ref) }.singleOrNull()

    public inline fun <reified T : GdmlGroup> getGroup(ref: String): T? =
        containers.filterIsInstance<GdmlStructure>().mapNotNull { it.get<T>(ref) }.singleOrNull()

    public var world: GdmlRef<GdmlGroup>
        get() = setup.world ?: error("The GDML structure does not contain a world volume")
        set(value) {
            //Check that the world volume is registered
            if (getGroup<GdmlGroup>(value.ref) == null) error("World element is not registered")
            setup.world = value
        }


    override fun toString(): String = encodeToString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Gdml

        if (containers != other.containers) return false
        if (setup != other.setup) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containers.hashCode()
        result = 31 * result + setup.hashCode()
        return result
    }

    public companion object {
        public inline operator fun invoke(block: Gdml.() -> Unit): Gdml = Gdml().apply(block)
    }
}

public inline fun <reified T : GdmlDefine> GdmlRef<T>.resolve(root: Gdml): T? = root.getDefine(ref)
public inline fun <reified T : GdmlSolid> GdmlRef<T>.resolve(root: Gdml): T? = root.getSolid(ref)
public inline fun <reified T : GdmlMaterial> GdmlRef<T>.resolve(root: Gdml): T? = root.getMaterial(ref)
public inline fun <reified T : GdmlGroup> GdmlRef<T>.resolve(root: Gdml): T? = root.getGroup(ref)

@Serializable
public sealed class GdmlContainer<T : GdmlNode> {

    public abstract val content: MutableList<T>

    @Transient
    private val cache: MutableMap<String, T?> = HashMap()

    /**
     * Directly add an item
     */
    public fun add(item: T) {
        content.add(item)
    }

    /**
     * Register an item with appropriate type and return a reference
     */
    public fun <R : T> register(item: R): GdmlRef<R> {
        val name = item.name
        val existingItem = getItem(name)
        if (existingItem == null) {
            add(item)
        } else if(existingItem == item) {
            //ignore addition
        } else {
            error("Redeclaration of GDML node with name '$name'")
        }
        return item.ref()
    }

    /**
     * A counter used for auto-naming
     */
    @Transient
    private var autoNameCounter = 0

    @OptIn(ExperimentalSerializationApi::class)
    @PublishedApi
    internal fun autoName(type: KType): String {
        val serialName = kotlinx.serialization.serializer(type).descriptor.serialName
        return "$serialName-${autoNameCounter++}"
    }

    /**
     * Use [providedName] if it is not null or generate unique automatic name
     */
    @PublishedApi
    internal inline fun <reified T : GdmlNode> resolveName(providedName: String?): String =
        providedName ?: autoName(typeOf<T>())

    /**
     * Get an Item from th container
     */
    public fun getItem(ref: String): T? = cache.getOrPut(ref) { content.find { it.name == ref } }

    public inline operator fun <reified R : T> get(ref: String): R? = getItem(ref) as? R

    override fun equals(other: Any?): Boolean = this === other || this.content == (other as? GdmlContainer<*>)?.content
    override fun hashCode(): Int = content.hashCode()
}

@Serializable
@SerialName("define")
public class GdmlDefineContainer : GdmlContainer<GdmlDefine>() {

    override val content: MutableList<GdmlDefine> = ArrayList()

    @GdmlApi
    public inline fun position(
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        name: String? = null,
        block: GdmlPosition.() -> Unit = {},
    ): GdmlRef<GdmlPosition> {
        val position = GdmlPosition(resolveName<GdmlPosition>(name)).apply(block).apply {
            this.name = resolveName<GdmlPosition>(name)
            this.x = x
            this.y = y
            this.z = z
        }

        return register(position)
    }

    @GdmlApi
    public inline fun rotation(
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        name: String? = null,
        block: GdmlRotation.() -> Unit = {},
    ): GdmlRef<GdmlRotation> {
        val rotation = GdmlRotation(resolveName<GdmlRotation>(name)).apply(block).apply {
            this.name = resolveName<GdmlRotation>(name)
            this.x = x
            this.y = y
            this.z = z
        }

        return register(rotation)
    }
}

@Serializable
@SerialName("materials")
public class GdmlMaterialContainer : GdmlContainer<GdmlMaterial>() {
    override val content: MutableList<GdmlMaterial> = ArrayList()

    public inline fun isotope(name: String? = null, build: GdmlIsotope.() -> Unit = {}): GdmlRef<GdmlIsotope> =
        register(GdmlIsotope(resolveName<GdmlIsotope>(name)).apply(build))

    public inline fun element(name: String? = null, build: GdmlElement.() -> Unit = {}): GdmlRef<GdmlElement> =
        register(GdmlElement(resolveName<GdmlIsotope>(name)).apply(build))

    public inline fun composite(name: String? = null, build: GdmlComposite.() -> Unit = {}): GdmlRef<GdmlComposite> =
        register(GdmlComposite(resolveName<GdmlIsotope>(name)).apply(build))

    public companion object {
        public val defaultMaterial: GdmlElement = GdmlElement("default")
    }
}

@Serializable
@SerialName("solids")
public class GdmlSolidContainer : GdmlContainer<GdmlSolid>() {
    override val content: MutableList<GdmlSolid> = ArrayList()

    @GdmlApi
    public inline fun box(
        x: Number,
        y: Number,
        z: Number,
        name: String? = null,
        block: GdmlBox.() -> Unit = {},
    ): GdmlRef<GdmlBox> = register(GdmlBox(resolveName<GdmlBox>(name), x, y, z).apply(block))

    @GdmlApi
    public inline fun sphere(
        rmax: Number,
        name: String? = null,
        block: GdmlSphere.() -> Unit = {},
    ): GdmlRef<GdmlSphere> = register(GdmlSphere(resolveName<GdmlSphere>(name), rmax = rmax).apply(block))


    @GdmlApi
    public inline fun orb(
        r: Number,
        name: String? = null,
        block: GdmlOrb.() -> Unit = {},
    ): GdmlRef<GdmlOrb> = register(GdmlOrb(resolveName<GdmlOrb>(name), r).apply(block))

    @GdmlApi
    public inline fun ellipsoid(
        ax: Number,
        by: Number,
        cz: Number,
        name: String? = null,
        block: GdmlEllipsoid.() -> Unit = {},
    ): GdmlRef<GdmlEllipsoid> = register(GdmlEllipsoid(resolveName<GdmlEllipsoid>(name), ax, by, cz).apply(block))


    @GdmlApi
    public inline fun eltube(
        dx: Number,
        dy: Number,
        dz: Number,
        name: String? = null,
        block: GdmlElTube.() -> Unit = {},
    ): GdmlRef<GdmlElTube> = register(GdmlElTube(resolveName<GdmlElTube>(name), dx, dy, dz).apply(block))

    @GdmlApi
    public inline fun elcone(
        dx: Number,
        dy: Number,
        zmax: Number,
        zcut: Number,
        name: String? = null,
        block: GdmlElCone.() -> Unit = {},
    ): GdmlRef<GdmlElCone> = register(GdmlElCone(resolveName<GdmlElCone>(name), dx, dy, zmax, zcut).apply(block))

    @GdmlApi
    public inline fun paraboloid(
        rlo: Number,
        rhi: Number,
        dz: Number,
        name: String? = null,
        block: GdmlParaboloid.() -> Unit = {},
    ): GdmlRef<GdmlParaboloid> = register(GdmlParaboloid(resolveName<GdmlParaboloid>(name), rlo, rhi, dz).apply(block))

    @GdmlApi
    public inline fun para(
        x: Number,
        y: Number,
        z: Number,
        name: String? = null,
        alpha: Number,
        theta: Number,
        phi: Number,
        block: GdmlParallelepiped.() -> Unit = {},
    ): GdmlRef<GdmlParallelepiped> = register(GdmlParallelepiped(
        resolveName<GdmlParallelepiped>(name),
        x, y, z, alpha, theta, phi
    ).apply(block))

    @GdmlApi
    public inline fun torus(
        rmin: Number,
        rmax: Number,
        rtor: Number,
        name: String? = null,
        block: GdmlTorus.() -> Unit = {},
    ): GdmlRef<GdmlTorus> = register(GdmlTorus(resolveName<GdmlTorus>(name), rmin, rmax, rtor).apply(block))

    @GdmlApi
    public inline fun trd(
        x1: Number,
        x2: Number,
        y1: Number,
        y2: Number,
        z: Number,
        name: String? = null,
        block: GdmlTrapezoid.() -> Unit = {},
    ): GdmlRef<GdmlTrapezoid> =
        register(GdmlTrapezoid(resolveName<GdmlTrapezoid>(name), x1, x2, y1, y2, z).apply(block))

    @GdmlApi
    public inline fun polyhedra(
        numsides: Int,
        name: String? = null,
        block: GdmlPolyhedra.() -> Unit = {},
    ): GdmlRef<GdmlPolyhedra> = register(GdmlPolyhedra(resolveName<GdmlPolyhedra>(name), numsides).apply(block))

    @GdmlApi
    public inline fun polycone(
        name: String? = null,
        block: GdmlPolycone.() -> Unit = {},
    ): GdmlRef<GdmlPolycone> = register(GdmlPolycone(resolveName<GdmlPolycone>(name)).apply(block))

    @GdmlApi
    public inline fun scaledSolid(
        solidref: GdmlRef<GdmlSolid>,
        scale: GdmlScale,
        name: String? = null,
        block: GdmlScaledSolid.() -> Unit = {},
    ): GdmlRef<GdmlScaledSolid> =
        register(GdmlScaledSolid(resolveName<GdmlScaledSolid>(name), solidref, scale).apply(block))

    @GdmlApi
    public inline fun tube(rmax: Number, z: Number, name: String, block: GdmlTube.() -> Unit = {}): GdmlRef<GdmlTube> =
        register(GdmlTube(name, rmax, z).apply(block))

    @GdmlApi
    public inline fun xtru(name: String, block: GdmlXtru.() -> Unit): GdmlRef<GdmlXtru> =
        register(GdmlXtru(name).apply(block))

    @GdmlApi
    public inline fun cone(
        z: Number,
        rmax1: Number,
        rmax2: Number,
        name: String? = null,
        block: GdmlCone.() -> Unit = {},
    ): GdmlRef<GdmlCone> {
        val cone = GdmlCone(resolveName<GdmlCone>(name), z, rmax1, rmax2).apply(block)
        return register(cone)
    }

    @GdmlApi
    public inline fun union(
        first: GdmlRef<GdmlSolid>,
        second: GdmlRef<GdmlSolid>,
        name: String? = null,
        block: GdmlUnion.() -> Unit = {},
    ): GdmlRef<GdmlUnion> {
        val union = GdmlUnion(resolveName<GdmlUnion>(name), first, second).apply(block)
        return register(union)
    }

    @GdmlApi
    public inline fun intersection(
        first: GdmlRef<GdmlSolid>,
        second: GdmlRef<GdmlSolid>,
        name: String? = null,
        block: GdmlIntersection.() -> Unit = {},
    ): GdmlRef<GdmlIntersection> {
        val intersection = GdmlIntersection(resolveName<GdmlIntersection>(name), first, second).apply(block)
        return register(intersection)
    }

    @GdmlApi
    public inline fun subtraction(
        first: GdmlRef<GdmlSolid>,
        second: GdmlRef<GdmlSolid>,
        name: String? = null,
        block: GdmlSubtraction.() -> Unit = {},
    ): GdmlRef<GdmlSubtraction> {
        val subtraction = GdmlSubtraction(resolveName<GdmlSubtraction>(name), first, second).apply(block)
        return register(subtraction)
    }
}

@Serializable
@GdmlApi
@SerialName("structure")
public class GdmlStructure : GdmlContainer<GdmlGroup>() {

    override val content: MutableList<GdmlGroup> = ArrayList()

    @GdmlApi
    public inline fun volume(
        materialref: GdmlRef<GdmlMaterial>,
        solidref: GdmlRef<GdmlSolid>,
        name: String? = null,
        block: GdmlVolume.() -> Unit = {},
    ): GdmlRef<GdmlVolume> {
        val res = GdmlVolume(resolveName<GdmlVolume>(name), materialref, solidref).apply(block)
        return register(res)
    }

    @GdmlApi
    public inline fun assembly(
        name: String? = null,
        block: GdmlAssembly.() -> Unit = {},
    ): GdmlRef<GdmlAssembly> {
        val res = GdmlAssembly(resolveName<GdmlAssembly>(name)).apply(block)
        return register(res)
    }
}

@Serializable
@SerialName("setup")
public class GdmlSetup(
    public var name: String = "Default",
    public var version: String = "1.0",
    @XmlSerialName("world", "", "")
    public var world: GdmlRef<GdmlGroup>? = null,
)

@GdmlApi
public fun Gdml.world(
    name: String = "world",
    block: GdmlAssembly.() -> Unit,
) {
    structure {
        assembly(name, block).also { setup.world = it }
    }
}