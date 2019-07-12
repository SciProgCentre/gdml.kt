@file:UseSerializers(NumberSerializer::class)

package scientifik.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

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
class GDMLScale(
    override var name: String,
    var x: Number = 1.0,
    var y: Number = 1.0,
    var z: Number = 1.0
) : GDMLDefine()

@Serializable
@SerialName("matrix")
class GDMLMatrix(
    override var name: String,
    var coldim: Int,
    var values: String
) : GDMLDefine()