@file:UseSerializers(NumberSerializer::class)

package kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
public data class GDMLDensity(var value: Double, var unit: String = "g/cm3")

//materials
@Serializable
public sealed class GDMLMaterial : GDMLNode {
    public var formula: String? = null

    @XmlSerialName("Z", "", "")
    public var z: Number? = null

    @XmlSerialName("N", "", "")
    public var n: Number? = null

    public val state: MaterialState = MaterialState.UNKNOWN

    @XmlSerialName("atom", "", "")
    public var atom: GDMLAtom? = null

    @XmlSerialName("D", "", "")
    public var density: GDMLDensity? = null

    @XmlSerialName("Dref", "", "")
    public var densityRef: GDMLRef<GDMLDefine>? = null
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
public data class GDMLAtom(var value: Number, var unit: String = "g/mole")

@Serializable
@SerialName("fraction")
public data class GDMLFraction(var n: Double, var ref: String)

@Serializable
@SerialName("isotope")
public data class GDMLIsotope(override var name: String) : GDMLMaterial()

@Serializable
@SerialName("element")
public data class GDMLElement(override var name: String) : GDMLMaterial()

@Serializable
@SerialName("material")
public data class GDMLComposite(override var name: String) : GDMLMaterial() {
    @XmlSerialName("fraction","","")
    public val fractions: ArrayList<GDMLFraction> = ArrayList()
}