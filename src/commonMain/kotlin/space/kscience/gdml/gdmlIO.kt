@file:OptIn(ExperimentalXmlUtilApi::class)

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
import nl.adaptivity.xmlutil.*
import nl.adaptivity.xmlutil.core.impl.multiplatform.StringWriter
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML

public object LUnitSerializer : KSerializer<LUnit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("lunit", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LUnit {
        return LUnit.valueOf(decoder.decodeString().uppercase())
    }

    override fun serialize(encoder: Encoder, value: LUnit) {
        encoder.encodeString(value.title)
    }
}

public object AUnitSerializer : KSerializer<AUnit> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("aunit", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AUnit {
        return AUnit.valueOf(decoder.decodeString().uppercase())
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
    UnknownChildHandler { input, _, _, name, candidates ->
        println(
            "Could not find a field for name $name${
                if (candidates.isNotEmpty()) candidates.joinToString(
                    prefix = "\n  candidates: "
                ) else ""
            } at position ${input.locationInfo}"
        )
        emptyList()
    }

internal val gdmlFormat: XML = XML(gdmlModule) {
    indent = 4
    xmlDeclMode = XmlDeclMode.Auto
    policy = DefaultXmlSerializationPolicy(
        pedantic = false,
        autoPolymorphic = true,
        unknownChildHandler = WARNING_UNKNOWN_CHILD_HANDLER
    )
}

/**
 * Decode Gdml from an xml string.
 */
public fun Gdml.Companion.decodeFromString(string: String, usePreprocessor: Boolean = false): Gdml =
    if (usePreprocessor) {
        val preprocessor = GdmlPreprocessor(XmlStreaming.newReader(string)) { parseAndEvaluate(it) }
        gdmlFormat.decodeFromReader(serializer(), preprocessor)
    } else {
        gdmlFormat.decodeFromString(serializer(), string)
    }

/**
 * Decode Gdml from an xml reader
 */
public fun Gdml.Companion.decodeFromReader(reader: XmlReader, usePreprocessor: Boolean = false): Gdml =
    if (usePreprocessor) {
        val preprocessor = GdmlPreprocessor(reader) { parseAndEvaluate(it) }
        gdmlFormat.decodeFromReader(serializer(), preprocessor)
    } else {
        gdmlFormat.decodeFromReader(serializer(), reader)
    }

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
    val xmlWriter =
        XmlStreaming.newWriter(stringWriter, gdmlFormat.config.repairNamespaces, gdmlFormat.config.xmlDeclMode)

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
