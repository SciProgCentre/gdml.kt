@file:UseSerializers(NumberSerializer::class)

package space.kscience.gdml

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
public sealed class GdmlDefine : GdmlNode

@Serializable
@SerialName("constant")
public class GdmlConstant(
    override var name: String,
    public var value: Number
) : GdmlDefine()

@Serializable
@SerialName("quantity")
public class GdmlQuantity(
    override var name: String,
    public var type: String? = null,
    public var value: Number,
    public var unit: String? = null
) : GdmlDefine()

@Serializable
@SerialName("variable")
public class GdmlVariable(
    override var name: String,
    public var value: String
) : GdmlDefine()

@Serializable
@SerialName("position")
public class GdmlPosition(
    public var x: Number = 0f,
    public var y: Number = 0f,
    public var z: Number = 0f,
    override var name: String = "",
    public var unit: LUnit? = null
) : GdmlDefine()

@Serializable
@SerialName("rotation")
public class GdmlRotation(
    public var x: Number = 0f,
    public var y: Number = 0f,
    public var z: Number = 0f,
    override var name: String = "",
    public var unit: AUnit? = null
) : GdmlDefine()

@Serializable
@SerialName("scale")
public class GdmlScale(
    public var x: Number = 1.0,
    public var y: Number = 1.0,
    public var z: Number = 1.0,
    override var name: String = ""
) : GdmlDefine()

@Serializable
@SerialName("matrix")
public class GdmlMatrix(
    override var name: String,
    public var coldim: Int,
    public var values: String
) : GdmlDefine()