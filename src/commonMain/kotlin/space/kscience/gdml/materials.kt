@file:UseSerializers(NumberSerializer::class)

package space.kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
public data class GdmlDensity(var value: Double, var unit: String = "g/cm3")

//materials
@Serializable
public sealed class GdmlMaterial : GdmlNode {
    public var formula: String? = null

    @XmlSerialName("Z", "", "")
    public var z: Number? = null

    @XmlSerialName("N", "", "")
    public var n: Number? = null

    public val state: MaterialState = MaterialState.UNKNOWN

    @XmlSerialName("atom", "", "")
    public var atom: GdmlAtom? = null

    @XmlSerialName("D", "", "")
    public var density: GdmlDensity? = null

    @XmlSerialName("Dref", "", "")
    public var densityRef: GdmlRef<GdmlDefine>? = null
}

@Serializable
public enum class MaterialState {
    @XmlSerialName("gas","","")
    GAS,

    @XmlSerialName("liquid","","")
    LIQUID,

    @XmlSerialName("solid","","")
    SOLID,

    @XmlSerialName("unknown","","")
    UNKNOWN
}

@Serializable
@SerialName("atom")
public data class GdmlAtom(var value: Number, var unit: String = "g/mole")

@Serializable
@SerialName("fraction")
public data class GdmlFraction(var n: Double, var ref: String)

@Serializable
@SerialName("isotope")
public data class GdmlIsotope(override var name: String) : GdmlMaterial()

@Serializable
@SerialName("element")
public data class GdmlElement(override var name: String) : GdmlMaterial()

@Serializable
@SerialName("material")
public data class GdmlComposite(override var name: String) : GdmlMaterial() {
    @XmlSerialName("fraction","","")
    public val fractions: ArrayList<GdmlFraction> = ArrayList()
}