@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class GDMLDensity(var value: Double, var unit: String = "g/cm3")

//materials
@Serializable
sealed class GDMLMaterial : GDMLNode {
    var formula: String? = null

    @XmlSerialName("Z", "", "")
    var z: Number? = null

    @XmlSerialName("N", "", "")
    var n: Number? = null

    val state: MaterialState = MaterialState.UNKNOWN

    @XmlSerialName("atom", "", "")
    var atom: GDMLAtom? = null

    @XmlSerialName("D", "", "")
    var density: GDMLDensity? = null

    @XmlSerialName("Dref", "", "")
    var densityRef: GDMLRef<GDMLDefine>? = null
}

@Serializable
enum class MaterialState {
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
data class GDMLAtom(var value: Number, var unit: String = "g/mole")

@Serializable
@SerialName("fraction")
data class GDMLFraction(var n: Double, var ref: String)

@Serializable
@SerialName("isotope")
data class GDMLIsotope(override var name: String) : GDMLMaterial()

@Serializable
@SerialName("element")
data class GDMLElement(override var name: String) : GDMLMaterial()

@Serializable
@SerialName("material")
data class GDMLComposite(override var name: String) : GDMLMaterial() {
    @XmlSerialName("fraction","","")
    val fractions = ArrayList<GDMLFraction>()
}