package scientifik.gdml

import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule

@Serializer(Number::class)
object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("kotlin.Number", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Number {
        return decoder.decodeDouble()
    }

    override fun serialize(encoder: Encoder, value: Number) {
        encoder.encodeDouble(value.toDouble())
    }
}

val gdmlModule = SerializersModule {
    polymorphic<GDMLContainer> {
        subclass(GDMLDefineContainer.serializer())
        subclass(GDMLMaterialContainer.serializer())
        subclass(GDMLSolidContainer.serializer())
        subclass(GDMLStructure.serializer())
    }
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
        subclass(GDMLCone.serializer())
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

fun GDML.stringify(): String = GDML.format.stringify(GDML.serializer(), this)
fun GDML.Companion.parse(string: String): GDML = format.parse(GDML.serializer(), string)