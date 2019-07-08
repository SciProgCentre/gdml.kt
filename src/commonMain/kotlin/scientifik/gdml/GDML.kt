@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XML

@DslMarker
annotation class GDMLApi

@GDMLApi
@Serializable
@SerialName("gdml")
class GDML {
    val define = GDMLDefineContainer()
    val materials = GDMLMaterialContainer()
    val solids = GDMLSolidContainer()
    val structure = GDMLStructure()
    val setup = GDMLSetup()


    fun define(block: GDMLDefineContainer.() -> Unit) {
        define.apply(block)
    }

    fun materials(block: GDMLMaterialContainer.() -> Unit) {
        materials.apply(block)
    }

    fun solids(block: GDMLSolidContainer.() -> Unit) {
        solids.apply(block)
    }

    fun structure(block: GDMLStructure.() -> Unit) {
        structure.apply(block)
    }

    inline fun <reified T : GDMLDefine> getDefine(ref: String): T? = define[ref]
    inline fun <reified T : GDMLSolid> getSolid(ref: String): T? = solids[ref]
    inline fun <reified T : GDMLMaterialBase> getMaterial(ref: String): T? = materials[ref]
    fun getVolume(ref: String): GDMLVolume? = structure[ref]

    val world: GDMLVolume
        get() = setup.world?.resolve(this)
            ?: structure.content.firstOrNull()
            ?: error("The GDML structure is empty")

    companion object {
        inline operator fun invoke(block: GDML.() -> Unit): GDML = GDML().apply(block)

        val format = XML(indent = 4, context = gdmlModule)
    }
}

inline fun <reified T : GDMLDefine> GDMLRef<T>.resolve(root: GDML): T? = root.getDefine(ref)
inline fun <reified T : GDMLSolid> GDMLRef<T>.resolve(root: GDML): T? = root.getSolid(ref)
inline fun <reified T : GDMLMaterialBase> GDMLRef<T>.resolve(root: GDML): T? = root.getMaterial(ref)
fun GDMLRef<GDMLVolume>.resolve(root: GDML): GDMLVolume? = root.getVolume(ref)

@GDMLApi
@Serializable
@SerialName("define")
class GDMLDefineContainer {
    val content = ArrayList<@ContextualSerialization GDMLDefine>()

    inline operator fun <reified T : GDMLDefine> get(ref: String): T? =
        content.filterIsInstance<T>().find { it.name == ref }

    fun position(name: String, x: Number = 0f, y: Number = 0f, z: Number = 0f, block: GDMLPosition.() -> Unit = {}) {
        content.add(GDMLPosition().apply(block).apply {
            this.name = name
            this.x = x
            this.y = y
            this.z = z
        })
    }

    fun rotation(name: String, x: Number = 0f, y: Number = 0f, z: Number = 0f, block: GDMLRotation.() -> Unit = {}) {
        content.add(
            GDMLRotation().apply(block).apply {
                this.name = name
                this.x = x
                this.y = y
                this.z = z
            }
        )
    }
}

@GDMLApi
@Serializable
@SerialName("materials")
class GDMLMaterialContainer {
    //TODO
    inline operator fun <reified T : GDMLMaterialBase> get(ref: String): T? = TODO()
}

@GDMLApi
@Serializable
@SerialName("solids")
class GDMLSolidContainer {
    val content = ArrayList<@ContextualSerialization GDMLSolid>()

    inline operator fun <reified T : GDMLSolid> get(ref: String): T? =
        content.filterIsInstance<T>().find { it.name == ref }

    fun box(name: String, x: Number = 0f, y: Number = 0f, z: Number = 0f, block: GDMLBox.() -> Unit = {}): GDMLBox {
        val box = GDMLBox(name, x, y, z).apply(block)
        content.add(box)
        return box
    }

    fun tube(name: String, rmax: Number, z: Number, block: GDMLTube.() -> Unit): GDMLTube {
        val tube = GDMLTube(name, rmax, z).apply(block)
        content.add(tube)
        return tube
    }

    fun xtru(name: String, block: GDMLXtru.() -> Unit): GDMLXtru {
        val xtru = GDMLXtru(name).apply(block)
        content.add(xtru)
        return xtru
    }

    fun union(
        name: String,
        first: GDMLSolid,
        second: GDMLSolid,
        block: GDMLUnion.() -> Unit
    ): GDMLUnion {
        val union = GDMLUnion(name, first.ref(), second.ref()).apply(block)
        content.add(union)
        return union
    }

    fun intersection(
        name: String,
        first: GDMLSolid,
        second: GDMLSolid,
        block: GDMLIntersection.() -> Unit
    ): GDMLIntersection {
        val intersection = GDMLIntersection(name, first.ref(), second.ref()).apply(block)
        content.add(intersection)
        return intersection
    }

    fun subtraction(
        name: String,
        first: GDMLSolid,
        second: GDMLSolid,
        block: GDMLSubtraction.() -> Unit
    ): GDMLSubtraction {
        val subtraction = GDMLSubtraction(name, first.ref(), second.ref()).apply(block)
        content.add(subtraction)
        return subtraction
    }
}

@GDMLApi
@Serializable
@SerialName("structure")
class GDMLStructure {
    val content = ArrayList<GDMLVolume>()

    operator fun get(ref: String): GDMLVolume? = content.find { it.name == ref }

    fun volume(
        name: String,
        materialref: GDMLRef<GDMLMaterialBase>,
        solidref: GDMLRef<GDMLSolid>,
        block: GDMLVolume.() -> Unit
    ): GDMLVolume {
        val res = GDMLVolume(name, materialref, solidref).apply(block)
        content.add(res)
        return res
    }
}

@Serializable
@SerialName("setup")
class GDMLSetup(var name: String = "Default", var version: String = "1.0", var world: GDMLRef<GDMLVolume>? = null)