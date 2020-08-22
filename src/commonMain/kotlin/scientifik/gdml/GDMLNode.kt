@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlPolyChildren
import nl.adaptivity.xmlutil.serialization.XmlSerialName

interface GDMLNode {
    var name: String
}

@Serializable
data class GDMLRef<out T : GDMLNode>(var ref: String)

fun <T : GDMLNode> ref(ref: String): GDMLRef<T> {
    return GDMLRef<T>(ref)
}

/**
 * Get a ref to this node
 */
fun <T : GDMLNode> T.ref(): GDMLRef<T> =
    if (name.isBlank()) error("Can't produce a ref for anonymous node") else GDMLRef(name)

// define block members


//Structure elements
@Serializable
sealed class GDMLPlacement

/**
 * Does not iherit [GDMLNode] since it does not have a name and could not be referenced
 */
@Serializable
@SerialName("physvol")
class GDMLPhysVolume(
    @XmlSerialName("volumeref", "", "")
    var volumeref: GDMLRef<GDMLGroup>,
    var name: String? = null
) : GDMLPlacement() {
    var copynumber: Int? = null

    @XmlSerialName("position", "", "")
    var position: GDMLPosition? = null

    @XmlSerialName("positionref", "", "")
    var positionref: GDMLRef<GDMLPosition>? = null

    @XmlSerialName("rotation", "", "")
    var rotation: GDMLRotation? = null

    @XmlSerialName("rotationref", "", "")
    var rotationref: GDMLRef<GDMLRotation>? = null

    @XmlSerialName("scale", "", "")
    var scale: GDMLScale? = null

    @XmlSerialName("scaleref", "", "")
    var scaleref: GDMLRef<GDMLScale>? = null
}

/**
 * Get the position from either position block or reference (if root is provided)
 */
fun GDMLPhysVolume.resolvePosition(root: GDML): GDMLPosition? = position ?: positionref?.resolve(root)

/**
 * Get the rotation from either position block or reference (if root is provided)
 */
fun GDMLPhysVolume.resolveRotation(root: GDML): GDMLRotation? = rotation ?: rotationref?.resolve(root)

/**
 * Get the scale from either position block or reference (if root is provided)
 */
fun GDMLPhysVolume.resolveScale(root: GDML): GDMLScale? = scale ?: scaleref?.resolve(root)

inline fun GDMLPhysVolume.position(block: GDMLPosition.() -> Unit) {
    position = GDMLPosition().apply(block)
}

inline fun GDMLPhysVolume.rotation(block: GDMLRotation.() -> Unit) {
    rotation = GDMLRotation().apply(block)
}

inline fun GDMLPhysVolume.scale(block: GDMLScale.() -> Unit) {
    scale = GDMLScale().apply(block)
}

/**
<...
axis=" xs:string [1]"
number=" ExpressionOrIDREFType [1]"
width=" ExpressionOrIDREFType [1]"
offset=" ExpressionOrIDREFType [1]"
unit=" xs:string [0..1]">
<volumeref> ReferenceType </volumeref> [1]
</...>
 */
@Serializable
@SerialName("divisionvol")
class GDMLDivisionVolume(
    var axis: String,
    var number: Number,
    var width: Number,
    var offset: Number,
    @XmlSerialName("volumeref", "", "")
    var volumeref: GDMLRef<GDMLVolume>,
    var unit: String = "mm"
) : GDMLPlacement()

@Serializable
sealed class GDMLGroup : GDMLNode {
    @XmlSerialName("physvol", "", "")
    val physVolumes = ArrayList<GDMLPhysVolume>()

    fun physVolume(volumeref: GDMLRef<GDMLGroup>, block: GDMLPhysVolume.() -> Unit): GDMLPhysVolume {
        val res = GDMLPhysVolume(volumeref).apply(block)
        physVolumes.add(res)
        return res
    }
}

fun GDMLGroup.physVolume(volume: GDMLGroup, block: GDMLPhysVolume.() -> Unit): GDMLPhysVolume =
    physVolume(volume.ref(), block)

@Serializable
@SerialName("assembly")
class GDMLAssembly(override var name: String) : GDMLGroup()

@Serializable
@SerialName("volume")
class GDMLVolume(
    override var name: String,
    @XmlSerialName("materialref", "", "")
    var materialref: GDMLRef<GDMLMaterial>,
    @XmlSerialName("solidref", "", "")
    var solidref: GDMLRef<GDMLSolid>
) : GDMLGroup() {

    @XmlPolyChildren(["physvol", "divisionvol"])
    @Polymorphic
    var placement: GDMLPlacement? = null
}