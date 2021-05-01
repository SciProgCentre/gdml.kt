package space.kscience.gdml

import nl.adaptivity.xmlutil.StAXReader
import org.intellij.lang.annotations.Language
import java.io.File
import java.net.URL

/**
 * Load materials from a standalone xml at given [url] in a separate gdml block.
 */
public fun Gdml.loadMaterialsFromUrl(url: URL): Unit = url.openStream().use { stream ->
    val xmlReader = StAXReader(stream, "UTF-8")
    val loadedMaterials = gdmlFormat.decodeFromReader(GdmlMaterialContainer.serializer(), xmlReader)
    materials.apply {
        loadedMaterials.content.forEach {
            add(it)
        }
    }
}

public fun Gdml.loadMaterialsFromUrl(@Language("http-url-reference") urlString: String): Unit =
    loadMaterialsFromUrl(URL(urlString))

/**
 * Load materials from a [file]
 */
public fun Gdml.loadMaterialsFromFile(file: File): Unit = file.inputStream().use { stream ->
    val xmlReader = StAXReader(stream, "UTF-8")
    val loadedMaterials = gdmlFormat.decodeFromReader(GdmlMaterialContainer.serializer(), xmlReader)
    containers.add(loadedMaterials)
}
