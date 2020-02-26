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
    polymorphic<GDMLMaterial> {
        GDMLIsotope::class with GDMLIsotope.serializer()
        GDMLElement::class with GDMLElement.serializer()
        GDMLComposite::class with GDMLComposite.serializer()
    }
    polymorphic<GDMLSolid> {
        GDMLBox::class with GDMLBox.serializer()
        GDMLTube::class with GDMLTube.serializer()
        GDMLXtru::class with GDMLXtru.serializer()
        GDMLPolyhedra::class with GDMLPolyhedra.serializer()
        addSubclass(GDMLCone.serializer())
        GDMLUnion::class with GDMLUnion.serializer()
        GDMLSubtraction::class with GDMLSubtraction.serializer()
        GDMLIntersection::class with GDMLIntersection.serializer()
        GDMLScaledSolid::class with GDMLScaledSolid.serializer()
    }
    polymorphic<GDMLGroup> {
        GDMLVolume::class with GDMLVolume.serializer()
        GDMLAssembly::class with GDMLAssembly.serializer()
    }
    polymorphic<GDMLPlacement> {
        GDMLPhysVolume::class with GDMLPhysVolume.serializer()
        GDMLDivisionVolume::class with GDMLDivisionVolume.serializer()
    }
}