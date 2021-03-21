@file:UseSerializers(NumberSerializer::class)

package space.kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.math.PI

//solids block members

@Serializable
public sealed class GdmlSolid : GdmlNode {
    public var lunit: LUnit? = null
    public var aunit: AUnit? = null
}

@Serializable
@SerialName("box")
public data class GdmlBox(
    override var name: String,
    var x: Number,
    var y: Number,
    var z: Number,
) : GdmlSolid()

@Serializable
@SerialName("sphere")
public data class GdmlSphere(
    override var name: String,
    var rmax: Number,
    var rmin: Number = 0f,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI,
    var starttheta: Number = 0f,
    var deltatheta: Number = PI,
) : GdmlSolid()

@Serializable
@SerialName("orb")
public data class GdmlOrb(
    override var name: String,
    var r: Number,
) : GdmlSolid()

@Serializable
@SerialName("ellipsoid")
public data class GdmlEllipsoid(
    override var name: String,
    var ax: Number,
    var by: Number,
    var cz: Number,
    var zcut1: Number? = null,
    var zcut2: Number? = null,
) : GdmlSolid()

@Serializable
@SerialName("eltube")
public data class GdmlElTube(
    override var name: String,
    var dx: Number,
    var dy: Number,
    var dz: Number,
) : GdmlSolid()

@Serializable
@SerialName("elcone")
public data class GdmlElCone(
    override var name: String,
    var dx: Number,
    var dy: Number,
    var zmax: Number,
    var zcut: Number,
) : GdmlSolid()

@Serializable
@SerialName("paraboloid")
public data class GdmlParaboloid(
    override var name: String,
    var rlo: Number,
    var rhi: Number,
    var dz: Number,
) : GdmlSolid()

@Serializable
@SerialName("para")
public data class GdmlParallelepiped(
    override var name: String,
    var x: Number,
    var y: Number,
    var z: Number,
    var alpha: Number,
    var theta: Number,
    var phi: Number,
) : GdmlSolid()

@Serializable
@SerialName("torus")
public data class GdmlTorus(
    override var name: String,
    var rmin: Number,
    var rmax: Number,
    var rtor: Number,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI,
) : GdmlSolid()

@Serializable
@SerialName("trd")
public data class GdmlTrapezoid(
    override var name: String,
    var x1: Number,
    var x2: Number,
    var y1: Number,
    var y2: Number,
    var z: Number,
) : GdmlSolid()

/**
<...
z=" ExpressionOrIDREFType [1]"
rmin=" ExpressionOrIDREFType [0..1]"
rmax=" ExpressionOrIDREFType [1]"/>
 */
@Serializable
@SerialName("zplane")
public data class GdmlZPlane(
    var z: Number,
    var rmax: Number,
    var rmin: Number = 0f,
)

public interface ZPlaneHolder {
    @XmlSerialName("zplane", "", "")
    public val planes: MutableList<GdmlZPlane>
}

public fun ZPlaneHolder.plane(z: Number, rmax: Number, block: GdmlZPlane.() -> Unit) {
    planes.add(GdmlZPlane(z, rmax).apply(block))
}

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
public data class GdmlPolyhedra(
    override var name: String,
    val numsides: Int,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI,
) : GdmlSolid(), ZPlaneHolder {
    @XmlSerialName("zplane", "", "")
    public override val planes: ArrayList<GdmlZPlane> = ArrayList()
}

@Serializable
@SerialName("polycone")
public data class GdmlPolycone(
    override var name: String,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI,
) : GdmlSolid(), ZPlaneHolder {
    @XmlSerialName("zplane", "", "")
    public override val planes: ArrayList<GdmlZPlane> = ArrayList()
}

@Serializable
@SerialName("tube")
public data class GdmlTube(
    override var name: String,
    var rmax: Number,
    var z: Number,
    var rmin: Number = 0f,
    var startphi: Number = 0f,
    var deltaphi: Number = 2 * PI,
) : GdmlSolid()

@Serializable
@SerialName("xtru")
public data class GdmlXtru(override var name: String) : GdmlSolid() {
    @Serializable
    @XmlSerialName("twoDimVertex", "", "")
    public data class TwoDimVertex(val x: Double, val y: Double)

    @Serializable
    @XmlSerialName("section", "", "")
    public data class Section(
        var zPosition: Number,
        var zOrder: Int,
        var xOffset: Number = 0.0,
        var yOffset: Number = 0.0,
        var scalingFactor: Number = 1.0,
    )

    @XmlSerialName("twoDimVertex", "", "")
    public val vertices: ArrayList<TwoDimVertex> = ArrayList()

    @XmlSerialName("section", "", "")
    public val sections: ArrayList<Section> = ArrayList()

    public fun vertex(x: Double, y: Double) {
        vertices.add(TwoDimVertex(x, y))
    }

    public fun section(index: Int, z: Double, block: Section.() -> Unit = {}) {
        sections.add(Section(zPosition = z, zOrder = index).apply(block))
    }
}

@Serializable
@SerialName("scaledSolid")
public data class GdmlScaledSolid(
    override var name: String,
    @XmlSerialName("solidref", "", "")
    val solidref: GdmlRef<GdmlSolid>,
    var scale: GdmlScale,
) : GdmlSolid()

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
public data class GdmlCone(
    override var name: String,
    var z: Number,
    var rmax1: Number,
    var rmax2: Number,
    var deltaphi: Number = 2 * PI,
    var rmin1: Number = 0f,
    var rmin2: Number = 0f,
    var startphi: Number = 0f,
) : GdmlSolid()

//boolean solids

@Serializable
public sealed class GdmlBoolSolid : GdmlSolid() {
    @XmlSerialName("first", "", "")
    public abstract var first: GdmlRef<GdmlSolid>

    @XmlSerialName("second", "", "")
    public abstract var second: GdmlRef<GdmlSolid>

    @XmlSerialName("position", "", "")
    public var position: GdmlPosition? = null

    @XmlSerialName("positionref", "", "")
    public var positionref: GdmlRef<GdmlPosition>? = null

    @XmlSerialName("rotation", "", "")
    public var rotation: GdmlRotation? = null

    @XmlSerialName("rotationref", "", "")
    public var rotationref: GdmlRef<GdmlRotation>? = null

    @XmlSerialName("firstposition", "", "")
    public var firstposition: GdmlPosition? = null

    @XmlSerialName("firstpositionref", "", "")
    public var firstpositionref: GdmlRef<GdmlPosition>? = null

    @XmlSerialName("firstrotation", "", "")
    public var firstrotation: GdmlRotation? = null

    @XmlSerialName("firstrotationref", "", "")
    public var firstrotationref: GdmlRef<GdmlRotation>? = null

    /**
     * Get the position from either position block or reference (if root is provided)
     */
    public fun resolvePosition(root: Gdml): GdmlPosition? = position ?: positionref?.resolve(root)

    /**
     * Get the rotation from either position block or reference (if root is provided)
     */
    public fun resolveRotation(root: Gdml): GdmlRotation? = rotation ?: rotationref?.resolve(root)

    public fun resolveFirstPosition(root: Gdml): GdmlPosition? = firstposition ?: firstpositionref?.resolve(root)
    public fun resolveFirstRotation(root: Gdml): GdmlRotation? = firstrotation ?: firstrotationref?.resolve(root)

    public fun position(
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GdmlPosition.() -> Unit = {},
    ) {
        position = GdmlPosition("$name.position", x, y, z).apply(block)
    }

    public fun rotation(
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GdmlRotation.() -> Unit = {},
    ) {
        rotation = GdmlRotation("$name.rotation", x, y, z).apply(block)
    }

    public fun firstposition(
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GdmlPosition.() -> Unit = {},
    ) {
        firstposition = GdmlPosition("$name.firstpostion", x, y, z).apply(block)
    }

    public fun firstrotation(
        x: Number = 0f,
        y: Number = 0f,
        z: Number = 0f,
        block: GdmlRotation.() -> Unit = {},
    ) {
        firstrotation = GdmlRotation("$name.firstrotation", x, y, z).apply(block)
    }
}


@Serializable
@SerialName("union")
public data class GdmlUnion(
    override var name: String,
    @XmlSerialName("first", "", "")
    override var first: GdmlRef<GdmlSolid>,
    @XmlSerialName("second", "", "")
    override var second: GdmlRef<GdmlSolid>,
) : GdmlBoolSolid()

@Serializable
@SerialName("subtraction")
public data class GdmlSubtraction(
    override var name: String,
    @XmlSerialName("first", "", "")
    override var first: GdmlRef<GdmlSolid>,
    @XmlSerialName("second", "", "")
    override var second: GdmlRef<GdmlSolid>,
) : GdmlBoolSolid()

@Serializable
@SerialName("intersection")
public data class GdmlIntersection(
    override var name: String,
    @XmlSerialName("first", "", "")
    override var first: GdmlRef<GdmlSolid>,
    @XmlSerialName("second", "", "")
    override var second: GdmlRef<GdmlSolid>,
) : GdmlBoolSolid()