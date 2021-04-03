@file:UseSerializers(NumberSerializer::class)
@file:Suppress("unused")

package space.kscience.gdml

import kotlinx.serialization.*
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import space.kscience.gdml.builder.GdmlSolidRegistry
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
        when (getItem(name)) {
            null -> add(item)
            item -> {
                //ignore addition
            }
            else -> error("Redeclaration of GDML node with name '$name'")
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
        val serialName = serializer(type).descriptor.serialName
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
public class GdmlSolidContainer : GdmlContainer<GdmlSolid>(), GdmlSolidRegistry {
    override val content: MutableList<GdmlSolid> = ArrayList()

    override fun <R : GdmlSolid> registerSolid(item: R): GdmlRef<R> = register(item)

    override fun resolveName(providedName: String?, type: KType): String = providedName ?: autoName(type)
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