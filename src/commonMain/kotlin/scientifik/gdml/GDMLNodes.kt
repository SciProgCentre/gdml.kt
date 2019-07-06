@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
sealed class GDMLNode {
    var name: String? = null
}

/**
 * Get a ref to this node
 */
fun GDMLNode.ref(): GDMLRef? = name?.let { GDMLRef(it) }

// define block members

@Serializable
@Polymorphic
sealed class GDMLDefine : GDMLNode()

@Serializable
@SerialName("position")
class GDMLPosition : GDMLDefine() {
    var x: Number = 0f
    var y: Number = 0f
    var z: Number = 0f
    var unit: String = "cm"
}

@Serializable
@SerialName("rotation")
class GDMLRotation : GDMLDefine() {
    var x: Number = 0f
    var y: Number = 0f
    var z: Number = 0f
    var unit: String = "deg"
}

//solids block members

@Serializable
sealed class GDMLSolid : GDMLNode()

@Serializable
@SerialName("box")
class GDMLBox : GDMLSolid() {
    var x: Number? = null
    var y: Number? = null
    var z: Number? = null
}

@Serializable
@SerialName("tube")
class GDMLTube : GDMLSolid() {
    var rmin: Number? = null
    var rmax: Number? = null
    var z: Number? = null
    var startphi: Number? = null
    var deltaphi: Number? = null
}

@Serializable
@SerialName("xtru")
class GDMLXtru : GDMLSolid() {
    @Serializable
    @SerialName("twoDimVertex")
    data class TwoDimVertex(val x: Double, val y: Double)

    @Serializable
    @SerialName("section")
    data class Section(
        var zOrder: Number? = null,
        var zPosition: Number? = null,
        var xOffsset: Number = 0.0,
        var yOffset: Number = 0.0,
        var scalingFactor: Number = 1.0
    )

    @SerialName("twoDimVertex")
    val vertices = ArrayList<TwoDimVertex>()

    @SerialName("section")
    val sections = ArrayList<Section>()

    fun vertex(x: Double, y: Double) {
        vertices.add(TwoDimVertex(x, y))
    }

    fun section(index: Int, z: Double, block: Section.() -> Unit = {}) {
        sections.add(Section(zOrder = index, zPosition = z).apply(block))
    }
}

@Serializable
data class GDMLRef(var ref: String)

@Serializable
@Polymorphic
sealed class GDMLBoolSolid : GDMLSolid() {
    @XmlSerialName("first","","")
    var first: GDMLRef? = null

    fun resolveFirst(root: GDML): GDMLSolid? = first?.let { root.getSolid(it.ref) }

    @XmlSerialName("second","","")
    var second: GDMLRef? = null

    fun resolveSecond(root: GDML): GDMLSolid? = second?.let { root.getSolid(it.ref) }

    @XmlSerialName("position","","")
    var position: GDMLPosition? = null

    @XmlSerialName("positionref","","")
    var positionref: GDMLRef? = null

    /**
     * Get the position from either position block or reference (if root is provided)
     */
    fun resolvePosition(root: GDML): GDMLDefine? = position ?: positionref?.let { root.getPosition(it.ref) }

    var rotation: GDMLRotation? = null

    var rotationref: GDMLRef? = null

    /**
     * Get the rotation from either position block or reference (if root is provided)
     */
    fun resolveRotation(root: GDML): GDMLRotation? = rotation ?: rotationref?.let { root.getRotation(it.ref) }

    var firstposition: GDMLPosition? = null

    var firstrotation: GDMLRotation? = null

    fun position(x: Number = 0f, y: Number = 0f, z: Number = 0f): GDMLPosition = GDMLPosition().apply {
        this.x = x
        this.y = y
        this.z = z
    }

    fun rotation(x: Number = 0f, y: Number = 0f, z: Number = 0f): GDMLRotation = GDMLRotation().apply {
        this.x = x
        this.y = y
        this.z = z
    }
}


@Serializable
@SerialName("union")
class GDMLUnion : GDMLBoolSolid()

@Serializable
@SerialName("subtraction")
class GDMLSubtraction : GDMLBoolSolid()

@Serializable
@SerialName("intersection")
class GDMLIntersection : GDMLBoolSolid()