@file:UseSerializers(NumberSerializer::class)

package kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@DslMarker
public annotation class GDMLApi

@Serializable
@SerialName("gdml")
public class GDML {

    public val containers: ArrayList<GDMLContainer<*>> = arrayListOf(
        GDMLDefineContainer(),
        GDMLMaterialContainer(),
        GDMLSolidContainer(),
        GDMLStructure()
    )

    public val setup: GDMLSetup = GDMLSetup()

    public val define: GDMLDefineContainer
        get() = (containers.filterIsInstance<GDMLDefineContainer>().firstOrNull()
            ?: GDMLDefineContainer().also { containers.add(0, it) })
    public val materials: GDMLMaterialContainer
        get() = (containers.filterIsInstance<GDMLMaterialContainer>().firstOrNull()
            ?: GDMLMaterialContainer().also { containers.add(it) })
    public val solids: GDMLSolidContainer
        get() = (containers.filterIsInstance<GDMLSolidContainer>().firstOrNull()
            ?: GDMLSolidContainer().also { containers.add(it) })
    public val structure: GDMLStructure
        get() = (containers.filterIsInstance<GDMLStructure>().firstOrNull()
            ?: GDMLStructure().also { containers.add(it) })

    @GDMLApi
    public inline fun define(block: GDMLDefineContainer.() -> Unit) {
        define.apply(block)
    }

    @GDMLApi
    public inline fun materials(block: GDMLMaterialContainer.() -> Unit) {
        materials.apply(block)
    }

    @GDMLApi
    public inline fun solids(block: GDMLSolidContainer.() -> Unit) {
        solids.apply(block)
    }

    @GDMLApi
    public inline fun structure(block: GDMLStructure.() -> Unit) {
        structure.apply(block)
    }

    public inline fun <reified T : GDMLDefine> getDefine(ref: String): T? =
        containers.filterIsInstance<GDMLDefineContainer>().map { it.get<T>(ref) }.single()

    public inline fun <reified T : GDMLMaterial> getMaterial(ref: String): T? =
        containers.filterIsInstance<GDMLMaterialContainer>().map { it.get<T>(ref) }.single()

    public inline fun <reified T : GDMLSolid> getSolid(ref: String): T? =
        containers.filterIsInstance<GDMLSolidContainer>().map { it.get<T>(ref) }.single()

    public inline fun <reified T : GDMLGroup> getGroup(ref: String): T? =
        containers.filterIsInstance<GDMLStructure>().map { it.get<T>(ref) }.single()

    public var world: GDMLGroup
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


    override fun toString(): String = format.stringify(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GDML

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
        public inline operator fun invoke(block: GDML.() -> Unit): GDML = GDML().apply(block)

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

public inline fun <reified T : GDMLDefine> GDMLRef<T>.resolve(root: GDML): T? = root.getDefine(ref)
public inline fun <reified T : GDMLSolid> GDMLRef<T>.resolve(root: GDML): T? = root.getSolid(ref)
public inline fun <reified T : GDMLMaterial> GDMLRef<T>.resolve(root: GDML): T? = root.getMaterial(ref)
public inline fun <reified T : GDMLGroup> GDMLRef<T>.resolve(root: GDML): T? = root.getGroup(ref)

@Serializable
public sealed class GDMLContainer<T : GDMLNode> {

    public abstract val content: MutableList<T>

    @Transient
    private val cache: MutableMap<String, T?> = HashMap()

    public fun add(item: T) {
        content.add(item)
    }

    public fun getMember(ref: String): T? = cache.getOrPut(ref) { content.find { it.name == ref } }

    public inline operator fun <reified R : T> get(ref: String): R? = getMember(ref) as? R

    override fun equals(other: Any?): Boolean = this === other || this.content == (other as? GDMLContainer<*>)?.content
    override fun hashCode(): Int = content.hashCode()
}

@Serializable
@SerialName("define")
public class GDMLDefineContainer : GDMLContainer<GDMLDefine>() {

    override val content: MutableList<GDMLDefine> = ArrayList()

    @GDMLApi
    public inline fun position(
        name: String,
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GDMLPosition.() -> Unit = {},
    ): GDMLPosition {
        return GDMLPosition().apply(block).apply {
            this.name = name
            this.x = x
            this.y = y
            this.z = z
        }.also {
            add(it)
        }
    }

    @GDMLApi
    public inline fun rotation(
        name: String,
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GDMLRotation.() -> Unit = {},
    ): GDMLRotation {
        return GDMLRotation().apply(block).apply {
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
public class GDMLMaterialContainer : GDMLContainer<GDMLMaterial>() {
    override val content: MutableList<GDMLMaterial> = ArrayList()

    public inline fun isotope(name: String, build: GDMLIsotope.() -> Unit): GDMLIsotope =
        GDMLIsotope(name).apply(build).also(::add)

    public inline fun element(name: String, build: GDMLElement.() -> Unit): GDMLElement =
        GDMLElement(name).apply(build).also(::add)

    public inline fun composite(name: String, build: GDMLComposite.() -> Unit): GDMLComposite =
        GDMLComposite(name).apply(build).also(::add)

    public companion object {
        public val defaultMaterial: GDMLElement = GDMLElement("default")
    }
}

@Serializable
@SerialName("solids")
public class GDMLSolidContainer : GDMLContainer<GDMLSolid>() {
    override val content: MutableList<GDMLSolid> = ArrayList()

    @GDMLApi
    public inline fun box(
        name: String,
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GDMLBox.() -> Unit = {},
    ): GDMLBox {
        val box = GDMLBox(name, x, y, z).apply(block)
        add(box)
        return box
    }

    @GDMLApi
    public inline fun tube(name: String, rmax: Number, z: Number, block: GDMLTube.() -> Unit): GDMLTube {
        val tube = GDMLTube(name, rmax, z).apply(block)
        add(tube)
        return tube
    }

    @GDMLApi
    public inline fun xtru(name: String, block: GDMLXtru.() -> Unit): GDMLXtru {
        val xtru = GDMLXtru(name).apply(block)
        add(xtru)
        return xtru
    }

    @GDMLApi
    public inline fun cone(
        name: String,
        z: Number,
        rmax1: Number,
        rmax2: Number,
        deltaphi: Number,
        block: GDMLCone.() -> Unit,
    ): GDMLCone {
        val cone = GDMLCone(name, z, rmax1, rmax2, deltaphi).apply(block)
        add(cone)
        return cone
    }

    @GDMLApi
    public inline fun union(
        name: String,
        first: GDMLSolid,
        second: GDMLSolid,
        block: GDMLUnion.() -> Unit,
    ): GDMLUnion {
        val union = GDMLUnion(name, first.ref(), second.ref()).apply(block)
        add(union)
        return union
    }

    @GDMLApi
    public inline fun intersection(
        name: String,
        first: GDMLSolid,
        second: GDMLSolid,
        block: GDMLIntersection.() -> Unit,
    ): GDMLIntersection {
        val intersection = GDMLIntersection(name, first.ref(), second.ref()).apply(block)
        add(intersection)
        return intersection
    }

    @GDMLApi
    public inline fun subtraction(
        name: String,
        first: GDMLSolid,
        second: GDMLSolid,
        block: GDMLSubtraction.() -> Unit,
    ): GDMLSubtraction {
        val subtraction = GDMLSubtraction(name, first.ref(), second.ref()).apply(block)
        add(subtraction)
        return subtraction
    }
}

@Serializable
@SerialName("structure")
public class GDMLStructure : GDMLContainer<GDMLGroup>() {

    override val content: MutableList<GDMLGroup> = ArrayList()

    @GDMLApi
    public inline fun volume(
        name: String,
        materialref: GDMLRef<GDMLMaterial>,
        solidref: GDMLRef<GDMLSolid>,
        block: GDMLVolume.() -> Unit = {},
    ): GDMLVolume {
        val res = GDMLVolume(name, materialref, solidref).apply(block)
        add(res)
        return res
    }

    @GDMLApi
    public inline fun volume(
        name: String,
        material: GDMLMaterial,
        solid: GDMLSolid,
        block: GDMLVolume.() -> Unit = {},
    ): GDMLVolume = volume(name, material.ref(), solid.ref(), block)

    @GDMLApi
    public inline fun assembly(
        name: String,
        block: GDMLAssembly.() -> Unit = {},
    ): GDMLAssembly {
        val res = GDMLAssembly(name).apply(block)
        add(res)
        return res
    }
}

@Serializable
@SerialName("setup")
public class GDMLSetup(
    public var name: String = "Default",
    public var version: String = "1.0",
    @XmlSerialName("world", "", "")
    public var world: GDMLRef<GDMLGroup>? = null,
)

@GDMLApi
public fun GDML.world(
    name: String = "world",
    block: GDMLAssembly.() -> Unit,
) {
    structure {
        assembly(name, block).also { setup.world = it.ref() }
    }
}