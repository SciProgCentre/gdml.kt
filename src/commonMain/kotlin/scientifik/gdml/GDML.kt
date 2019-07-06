@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.*

@DslMarker
annotation class GDMLApi

@GDMLApi
@Serializable
@SerialName("gdml")
class GDML {
    val define = GDMLDefineContainer()
    val solids = GDMLSolidContainer()

    fun define(block: GDMLDefineContainer.() -> Unit) {
        define.apply(block)
    }

    fun solids(block: GDMLSolidContainer.() -> Unit) {
        solids.apply(block)
    }

    fun getPosition(ref: String): GDMLDefine? = define[ref]
    fun getRotation(ref: String): GDMLRotation? = define[ref]
    inline fun <reified T : GDMLSolid> getSolid(ref: String): T? = solids[ref]

    companion object {
        inline operator fun invoke(block: GDML.() -> Unit): GDML = GDML().apply(block)
    }
}

@GDMLApi
@Serializable
@SerialName("define")
class GDMLDefineContainer {
    val content = ArrayList<@ContextualSerialization @Polymorphic GDMLDefine>()

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
@SerialName("solids")
class GDMLSolidContainer {
    val content = ArrayList<@ContextualSerialization GDMLSolid>()

    inline operator fun <reified T : GDMLSolid> get(ref: String): T? =
        content.filterIsInstance<T>().find { it.name == ref }

    fun box(name: String, x: Number = 0f, y: Number = 0f, z: Number = 0f, block: GDMLBox.() -> Unit = {}): GDMLBox {
        val box = GDMLBox().apply(block).apply {
            this.name = name
            this.x = x
            this.y = y
            this.z = z
        }
        content.add(box)
        return box
    }

    fun tube(name: String, block: GDMLTube.() -> Unit): GDMLTube {
        val tube = GDMLTube().apply(block).apply { this.name = name }
        content.add(tube)
        return tube
    }

    fun xtru(name: String, block: GDMLXtru.() -> Unit): GDMLXtru {
        val xtru = GDMLXtru().apply(block).apply { this.name = name }
        content.add(xtru)
        return xtru
    }

    fun union(
        name: String,
        first: GDMLSolid? = null,
        second: GDMLSolid? = null,
        block: GDMLUnion.() -> Unit
    ): GDMLUnion {
        val union = GDMLUnion().apply(block).apply {
            this.name = name
            this.first = first?.ref()
            this.second = second?.ref()
        }
        content.add(union)
        return union
    }

    fun intersection(
        name: String,
        first: GDMLSolid? = null,
        second: GDMLSolid? = null,
        block: GDMLIntersection.() -> Unit
    ): GDMLIntersection {
        val intersection = GDMLIntersection().apply(block).apply {
            this.name = name
            this.first = first?.ref()
            this.second = second?.ref()
        }
        content.add(intersection)
        return intersection
    }

    fun subtraction(
        name: String,
        first: GDMLSolid? = null,
        second: GDMLSolid? = null,
        block: GDMLSubtraction.() -> Unit
    ): GDMLSubtraction {
        val subtraction = GDMLSubtraction().apply(block).apply {
            this.name = name
            this.first = first?.ref()
            this.second = second?.ref()
        }
        content.add(subtraction)
        return subtraction
    }
}