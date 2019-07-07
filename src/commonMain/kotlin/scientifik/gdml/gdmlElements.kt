@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.math.PI

interface GDMLNode {
    var name: String
}

@Serializable
data class GDMLRef<T : GDMLNode>(var ref: String)

/**
 * Get a ref to this node
 */
fun <T : GDMLNode> T.ref(): GDMLRef<T>? = GDMLRef(name)

// define block members

@Serializable
sealed class GDMLDefine : GDMLNode

@Serializable
@SerialName("constant")
class GDMLConstant(
    override var name: String,
    var value: Number
) : GDMLDefine()

@Serializable
@SerialName("quantity")
class GDMLQuantity(
    override var name: String,
    var type: String? = null,
    var value: Number,
    var unit: String? = null
) : GDMLDefine()

@Serializable
@SerialName("variable")
class GDMLVariable(
    override var name: String,
    var value: String
) : GDMLDefine()

@Serializable
@SerialName("position")
class GDMLPosition(
    override var name: String = "",
    var x: Number = 0f,
    var y: Number = 0f,
    var z: Number = 0f,
    var unit: String = "cm"
) : GDMLDefine()

@Serializable
@SerialName("rotation")
class GDMLRotation(
    override var name: String = "",
    var x: Number = 0f,
    var y: Number = 0f,
    var z: Number = 0f,
    var unit: String = "deg"
) : GDMLDefine()

@Serializable
@SerialName("scale")
class GDMLScale(override var name: String, var value: Number) : GDMLDefine()

@Serializable
@SerialName("matrix")
class GDMLMatrix(
    override var name: String,
    var coldim: Int,
    var values: String
) : GDMLDefine()

//materials
@Serializable
sealed class GDMLMaterialBase : GDMLNode

@Serializable
@SerialName("isotope")
class GDMLIsotope(override var name: String) : GDMLMaterialBase()

@Serializable
@SerialName("element")
class GDMLElement(override var name: String) : GDMLMaterialBase()

@Serializable
@SerialName("material")
class GDMLMaterial(override var name: String) : GDMLMaterialBase()

//solids block members
//TODO add remaining solids

@Serializable
sealed class GDMLSolid : GDMLNode

@Serializable
@SerialName("box")
class GDMLBox(
    override var name: String,
    var x: Number,
    var y: Number,
    var z: Number
) : GDMLSolid()

@Serializable
@SerialName("tube")
class GDMLTube(
    override var name: String,
    var rmax: Number,
    var z: Number,
    var rmin: Number = 0f,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI
) : GDMLSolid()

@Serializable
@SerialName("xtru")
class GDMLXtru(override var name: String) : GDMLSolid() {
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
@SerialName("scaledSolid")
class GDMLScaledSolid(
    override var name: String,
    val solidref: GDMLRef<GDMLSolid>,
    var scale: GDMLScale
) : GDMLSolid()

//boolean solids

@Serializable
sealed class GDMLBoolSolid : GDMLSolid() {
    @XmlSerialName("first", "", "")
    var first: GDMLRef<GDMLSolid>? = null

    @XmlSerialName("second", "", "")
    var second: GDMLRef<GDMLSolid>? = null

    @XmlSerialName("position", "", "")
    var position: GDMLPosition? = null

    @XmlSerialName("positionref", "", "")
    var positionref: GDMLRef<GDMLPosition>? = null

    /**
     * Get the position from either position block or reference (if root is provided)
     */
    fun resolvePosition(root: GDML): GDMLDefine? = position ?: positionref?.resolve(root)

    @XmlSerialName("rotation", "", "")
    var rotation: GDMLRotation? = null

    @XmlSerialName("rotationref", "", "")
    var rotationref: GDMLRef<GDMLRotation>? = null

    /**
     * Get the rotation from either position block or reference (if root is provided)
     */
    fun resolveRotation(root: GDML): GDMLRotation? = rotation ?: rotationref?.resolve(root)

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
class GDMLUnion(override var name: String) : GDMLBoolSolid()

@Serializable
@SerialName("subtraction")
class GDMLSubtraction(override var name: String) : GDMLBoolSolid()

@Serializable
@SerialName("intersection")
class GDMLIntersection(override var name: String) : GDMLBoolSolid()

//Structure elements

/**
 * Does not iherit [GDMLNode] since it does not have a name and could not be referenced
 */
@Serializable
@SerialName("physvol")
class GDMLPhysVolume(var volumeref: GDMLRef<GDMLVolume>) {
    @XmlSerialName("position", "", "")
    var position: GDMLPosition? = null

    @XmlSerialName("positionref", "", "")
    var positionref: GDMLRef<GDMLPosition>? = null

    /**
     * Get the position from either position block or reference (if root is provided)
     */
    fun resolvePosition(root: GDML): GDMLDefine? = position ?: positionref?.resolve(root)

    @XmlSerialName("rotation", "", "")
    var rotation: GDMLRotation? = null

    @XmlSerialName("rotationref", "", "")
    var rotationref: GDMLRef<GDMLRotation>? = null

    /**
     * Get the rotation from either position block or reference (if root is provided)
     */
    fun resolveRotation(root: GDML): GDMLRotation? = rotation ?: rotationref?.resolve(root)
}

@Serializable
@SerialName("volume")
class GDMLVolume(
    override var name: String,
    var materialref: GDMLRef<GDMLMaterialBase>,
    var solidref: GDMLRef<GDMLSolid>
) : GDMLNode {
    val physVolumes = ArrayList<GDMLPhysVolume>()

    fun physVolume(volumeref: GDMLRef<GDMLVolume>,block: GDMLPhysVolume.()->Unit):GDMLPhysVolume{
        val res = GDMLPhysVolume(volumeref).apply(block)
        physVolumes.add(res)
        return res
    }
}