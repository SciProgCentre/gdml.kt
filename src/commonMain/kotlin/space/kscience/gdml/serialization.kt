package space.kscience.gdml

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
    polymorphic(GdmlContainer::class) {
        subclass(GdmlDefineContainer.serializer())
        subclass(GdmlMaterialContainer.serializer())
        subclass(GdmlSolidContainer.serializer())
        subclass(GdmlStructure.serializer())
    }
    polymorphic(GdmlDefine::class) {
        subclass(GdmlRotation.serializer())
        subclass(GdmlPosition.serializer())
        subclass(GdmlConstant.serializer())
        subclass(GdmlVariable.serializer())
        subclass(GdmlQuantity.serializer())
        subclass(GdmlScale.serializer())
    }
    polymorphic(GdmlMaterial::class) {
        subclass(GdmlIsotope.serializer())
        subclass(GdmlElement.serializer())
        subclass(GdmlComposite.serializer())
    }
    polymorphic(GdmlSolid::class  ) {
        subclass(GdmlBox.serializer())
        subclass(GdmlTube.serializer())
        subclass(GdmlXtru.serializer())
        subclass(GdmlPolyhedra.serializer())
        subclass(GdmlCone.serializer())
        subclass(GdmlPolycone.serializer())
        subclass(GdmlEllipsoid.serializer())
        subclass(GdmlElTube.serializer())
        subclass(GdmlElCone.serializer())
        subclass(GdmlParaboloid.serializer())
        subclass(GdmlParallelepiped.serializer())
        subclass(GdmlTorus.serializer())
        subclass(GdmlTrapezoid.serializer())
        subclass(GdmlUnion.serializer())
        subclass(GdmlSubtraction.serializer())
        subclass(GdmlIntersection.serializer())
        subclass(GdmlScaledSolid.serializer())
    }
    polymorphic(GdmlGroup::class  ) {
        subclass(GdmlVolume.serializer())
        subclass(GdmlAssembly.serializer())
    }
    polymorphic(GdmlPlacement::class  ) {
        subclass(GdmlPhysVolume.serializer())
        subclass(GdmlDivisionVolume.serializer())
    }
}

public fun Gdml.encodeToString(): String = Gdml.format.encodeToString(Gdml.serializer(), this)
public fun Gdml.Companion.decodeFromString(string: String): Gdml = format.decodeFromString(Gdml.serializer(), string)