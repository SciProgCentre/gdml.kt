@file:UseSerializers(NumberSerializer::class)

package space.kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
public data class GdmlDensity(var value: Double, var unit: String = "g/cm3")

//materials
@Serializable
public sealed class GdmlMaterial : GdmlNode {
    public var formula: String? = null

    @XmlSerialName("Z", "", "")
    public var z: Double? = null

    @XmlSerialName("N", "", "")
    public var n: Int? = null

    @XmlElement(false)
    public val state: MaterialState? = null

    @XmlSerialName("atom", "", "")
    public var atom: GdmlAtom? = null

    @XmlSerialName("D", "", "")
    public var density: GdmlDensity? = null

    @XmlSerialName("Dref", "", "")
    public var densityRef: GdmlRef<GdmlDefine>? = null
}

@Serializable
@SerialName("state")
public enum class MaterialState {
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
public data class GdmlAtom(var value: Number, var unit: String = "g/mole", val type: String = "A")

@Serializable
@SerialName("fraction")
public data class GdmlFraction(var n: Double, var ref: String) {
    public fun resolve(gdml: Gdml): GdmlMaterial? = gdml.getMaterial<GdmlMaterial>(ref)
}

@Serializable
@SerialName("isotope")
public data class GdmlIsotope(override var name: String) : GdmlMaterial()

@Serializable
@SerialName("element")
public data class GdmlElement(override var name: String) : GdmlMaterial() {
    @XmlSerialName("fraction", "", "")
    public val fractions: ArrayList<GdmlFraction> = ArrayList()

    /**
     * Add isotope fraction to the element
     */
    public fun fraction(n: Double, ref: GdmlRef<GdmlIsotope>) {
        fractions.add(GdmlFraction(n, ref.ref))
    }
}

@Serializable
@SerialName("material")
public data class GdmlComposite(override var name: String) : GdmlMaterial() {
    @XmlSerialName("fraction", "", "")
    public val fractions: ArrayList<GdmlFraction> = ArrayList()

    /**
     * Add fraction to the composite
     */
    public fun fraction(n: Double, ref: GdmlRef<GdmlMaterial>) {
        fractions.add(GdmlFraction(n, ref.ref))
    }

}
