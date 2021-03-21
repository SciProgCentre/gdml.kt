package space.kscience.gdml

import nl.adaptivity.xmlutil.StAXReader
import nl.adaptivity.xmlutil.StAXWriter
import nl.adaptivity.xmlutil.XmlDeclMode
import org.intellij.lang.annotations.Language
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
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
        Gdml.decodeFromReader(preprocessor)
    } else {
        Gdml.decodeFromReader(xmlReader)
    }
}

/**
 * Parse the file at [path] optionally using variable pre-processor with [usePreprocessor] flag
 *
 * **NOTE**: Preprocessor options could be expanded in future
 */
public fun Gdml.Companion.decodeFromFile(
    path: Path,
    usePreprocessor: Boolean = false,
): Gdml = Files.newInputStream(path, StandardOpenOption.READ).use {
    decodeFromStream(it, usePreprocessor)
}

/**
 * Parse the file at [file] optionally using variable pre-processor with [usePreprocessor] flag
 *
 * **NOTE**: Preprocessor options could be expanded in future
 */
public fun Gdml.Companion.decodeFromFile(file: File, usePreprocessor: Boolean = false): Gdml =
    decodeFromFile(file.toPath(), usePreprocessor = false)

/**
 * Fetch a gdml xml from given remote [url]
 */
public fun Gdml.Companion.decodeFromUrl(url: URL): Gdml = url.openStream().use {
    decodeFromStream(it)
}

/**
 * A shortcut to read [URL] with [urlString]
 */
public fun Gdml.Companion.decodeFromUrl(@Language("http-url-reference") urlString: String): Gdml =
    decodeFromUrl(URL(urlString))

/**
 * Write [gdml] to a given [stream] and flush stream afterwards.
 */
public fun Gdml.Companion.encodeToStream(gdml: Gdml, stream: OutputStream) {
    val xmlWriter = StAXWriter(stream, "UTF-8", false, XmlDeclMode.Auto)
    Gdml.encodeToWriter(gdml, xmlWriter)
    xmlWriter.flush()
    stream.flush()
}

/**
 * A shortcut to [Gdml.Companion.encodeToStream]
 */
public fun Gdml.encodeToStream(stream: OutputStream): Unit = Gdml.encodeToStream(this, stream)

/**
 * Encode a [gdml] to the [file] overwriting it content
 */
public fun Gdml.Companion.encodeToFile(gdml: Gdml, file: File) {
    file.outputStream().use {
        encodeToStream(gdml, it)
    }
}

public fun Gdml.encodeToFile(file: File): Unit = Gdml.encodeToFile(this, file)


/**
 * Encode a [gdml] to the [path] overwriting it content
 */
public fun Gdml.Companion.encodeToFile(gdml: Gdml, path: Path) {
    Files.newOutputStream(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE).use {
        encodeToStream(gdml, it)
    }
}

public fun Gdml.encodeToFile(path: Path): Unit = Gdml.encodeToFile(this, path)