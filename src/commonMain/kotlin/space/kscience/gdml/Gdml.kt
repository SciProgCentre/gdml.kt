@file:UseSerializers(NumberSerializer::class)
@file:Suppress("unused")

package space.kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlSerialName

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

    public var world: GdmlGroup
        get() = setup.world?.resolve(this)
            ?: error("The GDML structure does not contain a world volume")
        set(value) {
            //Add world element if it is not registered
            structure {
                if (getMember(value.name) == null) {
                    add(value)
                }
            }
            setup.world = value.ref()
        }


    override fun toString(): String = format.encodeToString(serializer(), this)

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

        private val WARNING_UNKNOWN_CHILD_HANDLER: UnknownChildHandler =
            { location, _, name, candidates ->
                println(
                    "Could not find a field for name $name${
                        if (candidates.isNotEmpty()) candidates.joinToString(
                            prefix = "\n  candidates: "
                        ) else ""
                    } at position $location"
                )
            }

        public val format: XML = XML(gdmlModule) {
            autoPolymorphic = true
            indent = 4
            unknownChildHandler = WARNING_UNKNOWN_CHILD_HANDLER
        }
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

    public fun add(item: T) {
        content.add(item)
    }

    public fun getMember(ref: String): T? = cache.getOrPut(ref) { content.find { it.name == ref } }

    public inline operator fun <reified R : T> get(ref: String): R? = getMember(ref) as? R

    override fun equals(other: Any?): Boolean = this === other || this.content == (other as? GdmlContainer<*>)?.content
    override fun hashCode(): Int = content.hashCode()
}

@Serializable
@SerialName("define")
public class GdmlDefineContainer : GdmlContainer<GdmlDefine>() {

    override val content: MutableList<GdmlDefine> = ArrayList()

    @GdmlApi
    public inline fun position(
        name: String,
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GdmlPosition.() -> Unit = {},
    ): GdmlPosition {
        return GdmlPosition().apply(block).apply {
            this.name = name
            this.x = x
            this.y = y
            this.z = z
        }.also {
            add(it)
        }
    }

    @GdmlApi
    public inline fun rotation(
        name: String,
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GdmlRotation.() -> Unit = {},
    ): GdmlRotation {
        return GdmlRotation().apply(block).apply {
            this.name = name
            this.x = x
            this.y = y
            this.z = z
        }.also {
            add(it)
        }
    }
}

@Serializable
@SerialName("materials")
public class GdmlMaterialContainer : GdmlContainer<GdmlMaterial>() {
    override val content: MutableList<GdmlMaterial> = ArrayList()

    public inline fun isotope(name: String, build: GdmlIsotope.() -> Unit): GdmlIsotope =
        GdmlIsotope(name).apply(build).also(::add)

    public inline fun element(name: String, build: GdmlElement.() -> Unit): GdmlElement =
        GdmlElement(name).apply(build).also(::add)

    public inline fun composite(name: String, build: GdmlComposite.() -> Unit): GdmlComposite =
        GdmlComposite(name).apply(build).also(::add)

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
        name: String,
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GdmlBox.() -> Unit = {},
    ): GdmlBox {
        val box = GdmlBox(name, x, y, z).apply(block)
        add(box)
        return box
    }

    @GdmlApi
    public inline fun tube(name: String, rmax: Number, z: Number, block: GdmlTube.() -> Unit): GdmlTube {
        val tube = GdmlTube(name, rmax, z).apply(block)
        add(tube)
        return tube
    }

    @GdmlApi
    public inline fun xtru(name: String, block: GdmlXtru.() -> Unit): GdmlXtru {
        val xtru = GdmlXtru(name).apply(block)
        add(xtru)
        return xtru
    }

    @GdmlApi
    public inline fun cone(
        name: String,
        z: Number,
        rmax1: Number,
        rmax2: Number,
        deltaphi: Number,
        block: GdmlCone.() -> Unit,
    ): GdmlCone {
        val cone = GdmlCone(name, z, rmax1, rmax2, deltaphi).apply(block)
        add(cone)
        return cone
    }

    @GdmlApi
    public inline fun union(
        name: String,
        first: GdmlSolid,
        second: GdmlSolid,
        block: GdmlUnion.() -> Unit,
    ): GdmlUnion {
        val union = GdmlUnion(name, first.ref(), second.ref()).apply(block)
        add(union)
        return union
    }

    @GdmlApi
    public inline fun intersection(
        name: String,
        first: GdmlSolid,
        second: GdmlSolid,
        block: GdmlIntersection.() -> Unit,
    ): GdmlIntersection {
        val intersection = GdmlIntersection(name, first.ref(), second.ref()).apply(block)
        add(intersection)
        return intersection
    }

    @GdmlApi
    public inline fun subtraction(
        name: String,
        first: GdmlSolid,
        second: GdmlSolid,
        block: GdmlSubtraction.() -> Unit,
    ): GdmlSubtraction {
        val subtraction = GdmlSubtraction(name, first.ref(), second.ref()).apply(block)
        add(subtraction)
        return subtraction
    }
}

@Serializable
@SerialName("structure")
public class GdmlStructure : GdmlContainer<GdmlGroup>() {

    override val content: MutableList<GdmlGroup> = ArrayList()

    @GdmlApi
    public inline fun volume(
        name: String,
        materialref: GdmlRef<GdmlMaterial>,
        solidref: GdmlRef<GdmlSolid>,
        block: GdmlVolume.() -> Unit = {},
    ): GdmlVolume {
        val res = GdmlVolume(name, materialref, solidref).apply(block)
        add(res)
        return res
    }

    @GdmlApi
    public inline fun volume(
        name: String,
        material: GdmlMaterial,
        solid: GdmlSolid,
        block: GdmlVolume.() -> Unit = {},
    ): GdmlVolume = volume(name, material.ref(), solid.ref(), block)

    @GdmlApi
    public inline fun assembly(
        name: String,
        block: GdmlAssembly.() -> Unit = {},
    ): GdmlAssembly {
        val res = GdmlAssembly(name).apply(block)
        add(res)
        return res
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
        assembly(name, block).also { setup.world = it.ref() }
    }
}