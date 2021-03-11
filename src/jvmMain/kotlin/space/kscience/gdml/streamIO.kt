package space.kscience.gdml

import nl.adaptivity.xmlutil.StAXReader
import nl.adaptivity.xmlutil.StAXWriter
import nl.adaptivity.xmlutil.XmlDeclMode
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Parse the [InputStream] optionally using variable pre-processor with [usePreprocessor] flag
 */
public fun Gdml.Companion.decodeFromStream(stream: InputStream, usePreprocessor: Boolean = false): Gdml {
    val xmlReader = StAXReader(stream, "UTF-8")
    return if (usePreprocessor) {
        val preprocessor = GdmlPreprocessor(xmlReader) { parseAndEvaluate(it) }
        format.decodeFromReader(serializer(), preprocessor)
    } else {
        format.decodeFromReader(serializer(), xmlReader)
    }
}

/**
 * Parse the file at [Path] optionally using variable pre-processor with [usePreprocessor] flag
 */
public fun Gdml.Companion.decodeFromFile(path: Path, usePreprocessor: Boolean = false): Gdml {
    return Files.newInputStream(path, StandardOpenOption.READ).use {
        decodeFromStream(it, usePreprocessor)
    }
}

public fun Gdml.encodeToStream(stream: OutputStream) {
    val xmlWriter = StAXWriter(stream, "UTF-8", false, XmlDeclMode.Auto)
    Gdml.format.encodeToWriter(xmlWriter, Gdml.serializer(), this)
}

public fun Gdml.encodeToFile(path: Path) {
    Files.newOutputStream(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE).use {
        encodeToStream(it)
        it.flush()
    }
}