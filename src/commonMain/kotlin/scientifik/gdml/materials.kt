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
    @SerialName("Z")
    var z: Number? = null
    @SerialName("N")
    var n: Number? = null

    val state: MaterialState = MaterialState.UNKNOWN

    var atom: GDMLAtom? = null

    @XmlSerialName("D", "", "")
    var density: GDMLDensity? = null
    @XmlSerialName("Dref", "", "")
    var densityRef: GDMLRef<GDMLDefine>? = null
}

enum class MaterialState {
    @SerialName("gas")
    GAS,
    @SerialName("liquid")
    LIQUID,
    @SerialName("solid")
    SOLID,
    @SerialName("unknown")
    UNKNOWN
}

@Serializable
@SerialName("atom")
data class GDMLAtom(var value: Number, var unit: String = "g/mole")

@Serializable
@SerialName("fraction")
class GDMLFraction(var n: Double, var ref: String)

@Serializable
@SerialName("isotope")
data class GDMLIsotope(override var name: String) : GDMLMaterial()

@Serializable
@SerialName("element")
data class GDMLElement(override var name: String) : GDMLMaterial()

@Serializable
@SerialName("material")
data class GDMLComposite(override var name: String) : GDMLMaterial() {
    @SerialName("fraction")
    val fractions = ArrayList<GDMLFraction>()
}