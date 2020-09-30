@file:UseSerializers(NumberSerializer::class)

package kscience.gdml

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlPolyChildren
import nl.adaptivity.xmlutil.serialization.XmlSerialName

public interface GDMLNode {
    public var name: String
}

@Serializable
public data class GDMLRef<out T : GDMLNode>(var ref: String)

public fun <T : GDMLNode> ref(ref: String): GDMLRef<T> {
    return GDMLRef<T>(ref)
}

/**
 * Get a ref to this node
 */
public fun <T : GDMLNode> T.ref(): GDMLRef<T> =
    if (name.isBlank()) error("Can't produce a ref for anonymous node") else GDMLRef(name)

// define block members


//Structure elements
@Serializable
public sealed class GDMLPlacement

/**
 * Does not iherit [GDMLNode] since it does not have a name and could not be referenced
 */
@Serializable
@SerialName("physvol")
public class GDMLPhysVolume(
    @XmlSerialName("volumeref", "", "")
    public var volumeref: GDMLRef<GDMLGroup>,
    public var name: String? = null
) : GDMLPlacement() {
    public var copynumber: Int? = null

    @XmlSerialName("position", "", "")
    public var position: GDMLPosition? = null

    @XmlSerialName("positionref", "", "")
    public var positionref: GDMLRef<GDMLPosition>? = null

    @XmlSerialName("rotation", "", "")
    public var rotation: GDMLRotation? = null

    @XmlSerialName("rotationref", "", "")
    public var rotationref: GDMLRef<GDMLRotation>? = null

    @XmlSerialName("scale", "", "")
    public var scale: GDMLScale? = null

    @XmlSerialName("scaleref", "", "")
    public var scaleref: GDMLRef<GDMLScale>? = null
}

/**
 * Get the position from either position block or reference (if root is provided)
 */
public fun GDMLPhysVolume.resolvePosition(root: GDML): GDMLPosition? = position ?: positionref?.resolve(root)

/**
 * Get the rotation from either position block or reference (if root is provided)
 */
public fun GDMLPhysVolume.resolveRotation(root: GDML): GDMLRotation? = rotation ?: rotationref?.resolve(root)

/**
 * Get the scale from either position block or reference (if root is provided)
 */
public fun GDMLPhysVolume.resolveScale(root: GDML): GDMLScale? = scale ?: scaleref?.resolve(root)

public inline fun GDMLPhysVolume.position(block: GDMLPosition.() -> Unit) {
    position = GDMLPosition().apply(block)
}

public inline fun GDMLPhysVolume.rotation(block: GDMLRotation.() -> Unit) {
    rotation = GDMLRotation().apply(block)
}

public inline fun GDMLPhysVolume.scale(block: GDMLScale.() -> Unit) {
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
public class GDMLDivisionVolume(
    public var axis: String,
    public var number: Number,
    public var width: Number,
    public var offset: Number,
    @XmlSerialName("volumeref", "", "")
    public var volumeref: GDMLRef<GDMLVolume>,
    public var unit: String = "mm"
) : GDMLPlacement()

@Serializable
public sealed class GDMLGroup : GDMLNode {
    @XmlSerialName("physvol", "", "")
    public val physVolumes: ArrayList<GDMLPhysVolume> = ArrayList<GDMLPhysVolume>()

    public fun physVolume(volumeref: GDMLRef<GDMLGroup>, block: GDMLPhysVolume.() -> Unit): GDMLPhysVolume {
        val res = GDMLPhysVolume(volumeref).apply(block)
        physVolumes.add(res)
        return res
    }
}

public fun GDMLGroup.physVolume(volume: GDMLGroup, block: GDMLPhysVolume.() -> Unit): GDMLPhysVolume =
    physVolume(volume.ref(), block)

@Serializable
@SerialName("assembly")
public class GDMLAssembly(override var name: String) : GDMLGroup()

@Serializable
@SerialName("volume")
public class GDMLVolume(
    override var name: String,
    @XmlSerialName("materialref", "", "")
    public var materialref: GDMLRef<GDMLMaterial>,
    @XmlSerialName("solidref", "", "")
    public var solidref: GDMLRef<GDMLSolid>
) : GDMLGroup() {

    @XmlPolyChildren(arrayOf("physvol", "divisionvol"))
    @Polymorphic
    public var placement: GDMLPlacement? = null
}