@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlSerialName

interface GDMLNode {
    var name: String
}

@Serializable
data class GDMLRef<T : GDMLNode>(var ref: String)

/**
 * Get a ref to this node
 */
fun <T : GDMLNode> T.ref(): GDMLRef<T> = GDMLRef(name)

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

    @XmlSerialName("rotation", "", "")
    var rotation: GDMLRotation? = null

    @XmlSerialName("rotationref", "", "")
    var rotationref: GDMLRef<GDMLRotation>? = null

    /**
     * Get the position from either position block or reference (if root is provided)
     */
    fun resolvePosition(root: GDML): GDMLPosition? = position ?: positionref?.resolve(root)

    /**
     * Get the rotation from either position block or reference (if root is provided)
     */
    fun resolveRotation(root: GDML): GDMLRotation? = rotation ?: rotationref?.resolve(root)
}

@Serializable
@SerialName("volume")
class GDMLVolume(
    override var name: String,
    @XmlSerialName("materialref", "", "")
    var materialref: GDMLRef<GDMLMaterialBase>,
    @XmlSerialName("solidref", "", "")
    var solidref: GDMLRef<GDMLSolid>
) : GDMLNode {
    val physVolumes = ArrayList<GDMLPhysVolume>()

    fun physVolume(volumeref: GDMLRef<GDMLVolume>, block: GDMLPhysVolume.() -> Unit): GDMLPhysVolume {
        val res = GDMLPhysVolume(volumeref).apply(block)
        physVolumes.add(res)
        return res
    }
}