package scientifik.gdml

import kotlinx.serialization.*
import kotlinx.serialization.internal.DoubleDescriptor
import kotlinx.serialization.modules.SerializersModule

@Serializer(Number::class)
object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = DoubleDescriptor

    override fun deserialize(decoder: Decoder): Number {
        return decoder.decodeDouble()
    }

    override fun serialize(encoder: Encoder, obj: Number) {
        encoder.encodeDouble(obj.toDouble())
    }
}

val gdmlModule = SerializersModule {
    polymorphic<GDMLDefine> {
        GDMLRotation::class with GDMLRotation.serializer()
        GDMLPosition::class with GDMLPosition.serializer()
        GDMLConstant::class with GDMLConstant.serializer()
        GDMLVariable::class with GDMLVariable.serializer()
        GDMLQuantity::class with GDMLQuantity.serializer()
        GDMLScale::class with GDMLScale.serializer()
    }
    polymorphic<GDMLMaterialBase> {
        GDMLIsotope::class with GDMLIsotope.serializer()
        GDMLElement::class with GDMLElement.serializer()
        GDMLMaterial::class with GDMLMaterial.serializer()
    }
    polymorphic<GDMLSolid> {
        GDMLBox::class with GDMLBox.serializer()
        GDMLTube::class with GDMLTube.serializer()
        GDMLXtru::class with GDMLXtru.serializer()
        GDMLUnion::class with GDMLUnion.serializer()
        GDMLSubtraction::class with GDMLSubtraction.serializer()
        GDMLIntersection::class with GDMLIntersection.serializer()
        GDMLScaledSolid::class with GDMLScaledSolid.serializer()
    }
}