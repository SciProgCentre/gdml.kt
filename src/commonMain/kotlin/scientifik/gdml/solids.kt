@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.math.PI

//solids block members
//TODO add remaining solids

@Serializable
sealed class GDMLSolid : GDMLNode {
    var lunit: String? = null
    var aunit: String? = null
}

@Serializable
@SerialName("box")
data class GDMLBox(
    override var name: String,
    var x: Number,
    var y: Number,
    var z: Number
) : GDMLSolid()

@Serializable
@SerialName("sphere")
data class GDMLSphere(
    override var name: String,
    var rmax: Number,
    var rmin: Number = 0f,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI,
    var starttheta: Number = 0f,
    var deltatheta: Number = PI
) : GDMLSolid()

@Serializable
@SerialName("orb")
data class GDMLOrb(
    override var name: String,
    var r: Number
) : GDMLSolid()

/**
<...
z=" ExpressionOrIDREFType [1]"
rmin=" ExpressionOrIDREFType [0..1]"
rmax=" ExpressionOrIDREFType [1]"/>
 */
@Serializable
@SerialName("zplane")
data class GDMLZPlane(
    var z: Number,
    var rmax: Number,
    var rmin: Number = 0f
)

/**
<polyhedra
lunit=" xs:string [0..1] ?"
aunit=" xs:string [0..1] ?"
name=" xs:ID [1]"
startphi=" ExpressionOrIDREFType [1]"
deltaphi=" ExpressionOrIDREFType [1]"
numsides=" ExpressionOrIDREFType [1]">
<zplane> ZPlaneType </zplane> [1..*]
</polyhedra>
 */
@Serializable
@SerialName("polyhedra")
data class GDMLPolyhedra(
    override var name: String,
    val numsides: Int,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI
) : GDMLSolid() {
    @XmlSerialName("zplane", "", "")
    val planes = ArrayList<GDMLZPlane>()
}

@Serializable
@SerialName("tube")
data class GDMLTube(
    override var name: String,
    var rmax: Number,
    var z: Number,
    var rmin: Number = 0f,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI
) : GDMLSolid()

@Serializable
@SerialName("xtru")
data class GDMLXtru(override var name: String) : GDMLSolid() {
    @Serializable
    @SerialName("twoDimVertex")
    data class TwoDimVertex(val x: Double, val y: Double)

    @Serializable
    @SerialName("section")
    data class Section(
        var zPosition: Number,
        var zOrder: Int,
        var xOffset: Number = 0.0,
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
        sections.add(Section(zPosition = z, zOrder = index).apply(block))
    }
}

@Serializable
@SerialName("scaledSolid")
data class GDMLScaledSolid(
    override var name: String,
    @XmlSerialName("solidref", "", "")
    val solidref: GDMLRef<GDMLSolid>,
    var scale: GDMLScale
) : GDMLSolid()

/*
<xs:element name="cone" substitutionGroup="Solid">
    <xs:annotation>
    <xs:documentation>
    CSG cone or cone segment described by rmin1 inside radius at z/2 rmin2 inside radius at z/2 rmax1 outside radius at z/2 rmax2 outside radius at z/2 z length in z startphi starting angle of the segment in radians deltaphi delta angle of the segment in radians
    </xs:documentation>
    </xs:annotation>
    <xs:complexType>
    <xs:complexContent>
    <xs:extension base="SolidType">
        <xs:attribute name="z" type="ExpressionOrIDREFType" use="required"/>
        <xs:attribute default="0.0" name="rmin1" type="ExpressionOrIDREFType"/>
        <xs:attribute default="0.0" name="rmin2" type="ExpressionOrIDREFType"/>
        <xs:attribute name="rmax1" type="ExpressionOrIDREFType" use="required"/>
        <xs:attribute name="rmax2" type="ExpressionOrIDREFType" use="required"/>
        <xs:attribute default="0.0" name="startphi" type="ExpressionOrIDREFType"/>
        <xs:attribute name="deltaphi" type="ExpressionOrIDREFType" use="required"/>
    </xs:extension>
    </xs:complexContent>
    </xs:complexType>
</xs:element>
 */
@Serializable
@SerialName("cone")
data class GDMLCone(
    override var name: String,
    var z: Number,
    var rmax1: Number,
    var rmax2: Number,
    var deltaphi: Number,
    var rmin1: Number = 0f,
    var rmin2: Number = 0f,
    var startphi: Number = 0f
) : GDMLSolid()

//boolean solids

@Serializable
sealed class GDMLBoolSolid : GDMLSolid() {
    @XmlSerialName("first", "", "")
    abstract var first: GDMLRef<GDMLSolid>

    @XmlSerialName("second", "", "")
    abstract var second: GDMLRef<GDMLSolid>

    @XmlSerialName("position", "", "")
    var position: GDMLPosition? = null

    @XmlSerialName("positionref", "", "")
    var positionref: GDMLRef<GDMLPosition>? = null

    @XmlSerialName("rotation", "", "")
    var rotation: GDMLRotation? = null

    @XmlSerialName("rotationref", "", "")
    var rotationref: GDMLRef<GDMLRotation>? = null

    @XmlSerialName("firstposition", "", "")
    var firstposition: GDMLPosition? = null

    @XmlSerialName("firstpositionref", "", "")
    var firstpositionref: GDMLRef<GDMLPosition>? = null

    @XmlSerialName("firstrotation", "", "")
    var firstrotation: GDMLRotation? = null

    @XmlSerialName("firstrotationref", "", "")
    var firstrotationref: GDMLRef<GDMLRotation>? = null

    /**
     * Get the position from either position block or reference (if root is provided)
     */
    fun resolvePosition(root: GDML): GDMLPosition? = position ?: positionref?.resolve(root)

    /**
     * Get the rotation from either position block or reference (if root is provided)
     */
    fun resolveRotation(root: GDML): GDMLRotation? = rotation ?: rotationref?.resolve(root)

    fun resolveFirstPosition(root: GDML): GDMLPosition? = firstposition ?: firstpositionref?.resolve(root)
    fun resolveFirstRotation(root: GDML): GDMLRotation? = firstrotation ?: firstrotationref?.resolve(root)

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
data class GDMLUnion(
    override var name: String,
    @XmlSerialName("first", "", "")
    override var first: GDMLRef<GDMLSolid>,
    @XmlSerialName("second", "", "")
    override var second: GDMLRef<GDMLSolid>
) : GDMLBoolSolid()

@Serializable
@SerialName("subtraction")
data class GDMLSubtraction(
    override var name: String,
    @XmlSerialName("first", "", "")
    override var first: GDMLRef<GDMLSolid>,
    @XmlSerialName("second", "", "")
    override var second: GDMLRef<GDMLSolid>
) : GDMLBoolSolid()

@Serializable
@SerialName("intersection")
data class GDMLIntersection(
    override var name: String,
    @XmlSerialName("first", "", "")
    override var first: GDMLRef<GDMLSolid>,
    @XmlSerialName("second", "", "")
    override var second: GDMLRef<GDMLSolid>
) : GDMLBoolSolid()