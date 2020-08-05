package scientifik.gdml

import nl.adaptivity.xmlutil.StAXReader
import nl.adaptivity.xmlutil.StAXWriter
import nl.adaptivity.xmlutil.XmlDeclMode
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

fun GDML.Companion.parse(stream: InputStream): GDML {
    val xmlReader = StAXReader(stream, "UTF-8")
    return format.parse(GDML.serializer(), xmlReader)
}

fun GDML.Companion.parseFile(path: Path): GDML {
    return Files.newInputStream(path, StandardOpenOption.READ).use {
        parse(it)
    }
}

fun GDML.dump(stream: OutputStream) {
    val xmlWriter = StAXWriter(stream, "UTF-8", false, XmlDeclMode.Auto)
    GDML.format.toXml(xmlWriter, GDML.serializer(), this)
}

fun GDML.dumpToFile(path: Path) {
    Files.newOutputStream(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE).use {
        dump(it)
    }
}