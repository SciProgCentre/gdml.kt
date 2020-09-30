@file:UseSerializers(NumberSerializer::class)

package kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
public sealed class GDMLDefine : GDMLNode

@Serializable
@SerialName("constant")
public class GDMLConstant(
    override var name: String,
    public var value: Number
) : GDMLDefine()

@Serializable
@SerialName("quantity")
public class GDMLQuantity(
    override var name: String,
    public var type: String? = null,
    public var value: Number,
    public var unit: String? = null
) : GDMLDefine()

@Serializable
@SerialName("variable")
public class GDMLVariable(
    override var name: String,
    public var value: String
) : GDMLDefine()

@Serializable
@SerialName("position")
public class GDMLPosition(
    override var name: String = "",
    public var x: Number = 0f,
    public var y: Number = 0f,
    public var z: Number = 0f,
    public var unit: String = "cm"
) : GDMLDefine()

@Serializable
@SerialName("rotation")
public class GDMLRotation(
    override var name: String = "",
    public var x: Number = 0f,
    public var y: Number = 0f,
    public var z: Number = 0f,
    public var unit: String = "deg"
) : GDMLDefine()

@Serializable
@SerialName("scale")
public class GDMLScale(
    override var name: String = "",
    public var x: Number = 1.0,
    public var y: Number = 1.0,
    public var z: Number = 1.0
) : GDMLDefine()

@Serializable
@SerialName("matrix")
public class GDMLMatrix(
    override var name: String,
    public var coldim: Int,
    public var values: String
) : GDMLDefine()