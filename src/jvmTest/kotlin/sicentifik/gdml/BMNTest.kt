package sicentifik.gdml

import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.StAXReader
import org.junit.Test
import scientifik.gdml.GDML
import scientifik.gdml.GDMLDefine
import scientifik.gdml.GDMLMaterial
import scientifik.gdml.GDMLSolid
import java.io.File
import java.net.URL

class BMNTest {

    @Test
    fun printChildren() {
        println(
            GDMLDefine::class.sealedSubclasses.joinToString(
                prefix = "[\"",
                separator = "\", \"",
                postfix = "\"]"
            ) { it.serializer().descriptor.name })

        println(
            GDMLMaterial::class.sealedSubclasses.joinToString(
                prefix = "[\"",
                separator = "\", \"",
                postfix = "\"]"
            ) { it.serializer().descriptor.name })

        println(
            GDMLSolid::class.sealedSubclasses.joinToString(
                prefix = "[\"",
                separator = "\", \"",
                postfix = "\"]"
            ) { it.serializer().descriptor.name })
    }

    @Test
    fun testRead() {
        val url = URL("https://drive.google.com/open?id=1w5e7fILMN83JGgB8WANJUYm8OW2s0WVO")
        val file = File("D:\\Work\\Projects\\gdml.kt\\src\\commonTest\\resources\\gdml\\geofile_full.xml")
        val stream = if(file.exists()){
            file.inputStream()
        } else {
            url.openStream()
        }

        val xmlReader = StAXReader(stream, "UTF-8")
        val xml = GDML.format.parse(GDML::class, xmlReader)
    }
}