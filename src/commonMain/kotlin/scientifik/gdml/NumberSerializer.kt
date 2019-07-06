package scientifik.gdml

import kotlinx.serialization.*
import kotlinx.serialization.internal.DoubleDescriptor

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