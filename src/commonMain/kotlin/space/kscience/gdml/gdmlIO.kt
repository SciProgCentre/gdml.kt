package space.kscience.gdml

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.XmlStreaming
import nl.adaptivity.xmlutil.XmlWriter
import nl.adaptivity.xmlutil.core.impl.multiplatform.StringWriter
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlSerializationPolicy

public object LUnitSerializer : KSerializer<LUnit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("lunit", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LUnit {
        return LUnit.valueOf(decoder.decodeString().toUpperCase())
    }

    override fun serialize(encoder: Encoder, value: LUnit) {
        encoder.encodeString(value.title)
    }
}

public object AUnitSerializer : KSerializer<AUnit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("aunit", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AUnit {
        return AUnit.valueOf(decoder.decodeString().toUpperCase())
    }

    override fun serialize(encoder: Encoder, value: AUnit) {
        encoder.encodeString(value.title)
    }
}

public object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kotlin.Number", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Number {
        return decoder.decodeDouble()
    }

    override fun serialize(encoder: Encoder, value: Number) {
        encoder.encodeDouble(value.toDouble())
    }
}

internal val gdmlModule: SerializersModule = SerializersModule {
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
    polymorphic(GdmlSolid::class) {
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
    polymorphic(GdmlGroup::class) {
        subclass(GdmlVolume.serializer())
        subclass(GdmlAssembly.serializer())
    }
    polymorphic(GdmlPlacement::class) {
        subclass(GdmlPhysVolume.serializer())
        subclass(GdmlDivisionVolume.serializer())
    }
}

private val WARNING_UNKNOWN_CHILD_HANDLER: UnknownChildHandler =
    { location, _, name, candidates ->
        println(
            "Could not find a field for name $name${
                if (candidates.isNotEmpty()) candidates.joinToString(
                    prefix = "\n  candidates: "
                ) else ""
            } at position $location"
        )
    }

internal val gdmlFormat: XML = XML(gdmlModule) {
    autoPolymorphic = true
    indent = 4
    unknownChildHandler = WARNING_UNKNOWN_CHILD_HANDLER
    xmlDeclMode = XmlDeclMode.Auto
    policy = DefaultXmlSerializationPolicy(
        pedantic = true,
        autoPolymorphic = true,
        encodeDefault = XmlSerializationPolicy.XmlEncodeDefault.NEVER
    )
}

/**
 * Decode Gdml from an xml string
 */
public fun Gdml.Companion.decodeFromString(string: String): Gdml =
    gdmlFormat.decodeFromString(serializer(), string)

/**
 * Decode Gdml from an xml reader
 */
public fun Gdml.Companion.decodeFromReader(reader: XmlReader): Gdml =
    gdmlFormat.decodeFromReader(serializer(), reader)

/**
 * Write gdml to provided xml writer
 */
public fun Gdml.Companion.encodeToWriter(gdml: Gdml, writer: XmlWriter): Unit =
    gdmlFormat.encodeToWriter(GdmlPostProcessor(writer), serializer(), gdml)

/**
 * Encode gdml to an xml string
 */
public fun Gdml.Companion.encodeToString(gdml: Gdml): String {
    val stringWriter = StringWriter()
    val xmlWriter = XmlStreaming.newWriter(stringWriter, gdmlFormat.config.repairNamespaces, gdmlFormat.config.xmlDeclMode)

    var ex: Throwable? = null
    try {
        encodeToWriter(gdml, xmlWriter)
    } catch (e: Throwable) {
        ex = e
    } finally {
        try {
            xmlWriter.close()
        } finally {
            ex?.let { throw it }
        }

    }
    return stringWriter.toString()
}

/**
 * A shortcut to encode gdml to string using [Gdml.Companion.encodeToString]
 */
public fun Gdml.encodeToString(): String = Gdml.encodeToString(this)

/**
 * A shortcut to write gdml to writer using [Gdml.Companion.encodeToWriter]
 */
public fun Gdml.encodeToWriter(writer: XmlWriter): Unit = Gdml.encodeToWriter(this, writer)
