package kscience.gdml

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


@OptIn(ExperimentalSerializationApi::class)
@Serializer(Number::class)
public object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kotlin.Number", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Number {
        return decoder.decodeDouble()
    }

    override fun serialize(encoder: Encoder, value: Number) {
        encoder.encodeDouble(value.toDouble())
    }
}

public val gdmlModule: SerializersModule = SerializersModule {
    polymorphic(GDMLContainer::class) {
        subclass(GDMLDefineContainer.serializer())
        subclass(GDMLMaterialContainer.serializer())
        subclass(GDMLSolidContainer.serializer())
        subclass(GDMLStructure.serializer())
    }
    polymorphic(GDMLDefine::class) {
        subclass(GDMLRotation.serializer())
        subclass(GDMLPosition.serializer())
        subclass(GDMLConstant.serializer())
        subclass(GDMLVariable.serializer())
        subclass(GDMLQuantity.serializer())
        subclass(GDMLScale.serializer())
    }
    polymorphic(GDMLMaterial::class) {
        subclass(GDMLIsotope.serializer())
        subclass(GDMLElement.serializer())
        subclass(GDMLComposite.serializer())
    }
    polymorphic(GDMLSolid::class  ) {
        subclass(GDMLBox.serializer())
        subclass(GDMLTube.serializer())
        subclass(GDMLXtru.serializer())
        subclass(GDMLPolyhedra.serializer())
        subclass(GDMLCone.serializer())
        subclass(GDMLPolycone.serializer())
        subclass(GDMLEllipsoid.serializer())
        subclass(GDMLElTube.serializer())
        subclass(GDMLElCone.serializer())
        subclass(GDMLParaboloid.serializer())
        subclass(GDMLParallelepiped.serializer())
        subclass(GDMLTorus.serializer())
        subclass(GDMLTrapezoid.serializer())
        subclass(GDMLUnion.serializer())
        subclass(GDMLSubtraction.serializer())
        subclass(GDMLIntersection.serializer())
        subclass(GDMLScaledSolid.serializer())
    }
    polymorphic(GDMLGroup::class  ) {
        subclass(GDMLVolume.serializer())
        subclass(GDMLAssembly.serializer())
    }
    polymorphic(GDMLPlacement::class  ) {
        subclass(GDMLPhysVolume.serializer())
        subclass(GDMLDivisionVolume.serializer())
    }
}

public fun GDML.encodeToString(): String = GDML.format.encodeToString(GDML.serializer(), this)
public fun GDML.Companion.decodeFromString(string: String): GDML = format.decodeFromString(GDML.serializer(), string)