package scientifik.gdml

import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.StAXReader
import org.junit.Test
import scientifik.gdml.*
import java.io.File
import kotlin.test.Ignore

class PredefinedGeometryTest {

    @Test
    fun printChildren() {
        println(
            GDMLDefine::class.sealedSubclasses.joinToString(
                prefix = "[\"",
                separator = "\", \"",
                postfix = "\"]"
            ) { it.serializer().descriptor.serialName })

        println(
            GDMLMaterial::class.sealedSubclasses.joinToString(
                prefix = "[\"",
                separator = "\", \"",
                postfix = "\"]"
            ) { it.serializer().descriptor.serialName })

        println(
            GDMLSolid::class.sealedSubclasses.joinToString(
                prefix = "[\"",
                separator = "\", \"",
                postfix = "\"]"
            ) { it.serializer().descriptor.serialName })
    }

    @Test
    fun testReadBMN() {
        val file = File("gdml-source/BM@N.gdml")

        val xmlReader = StAXReader(file.inputStream(), "UTF-8")
        val gdml = GDML.format.parse(GDML::class, xmlReader)
        println(gdml.world)
        val ref = GDMLRef<GDMLAssembly>("Magnet")
        val magnet = ref.resolve(gdml)!!
        println(magnet)
    }

    @Test
    fun testReadCubes() {
        val file = File("gdml-source/cubes.gdml")

        val xmlReader = StAXReader(file.inputStream(), "UTF-8")
        val gdml = GDML.format.parse(GDML::class, xmlReader)
        println(gdml.world)
    }

    @Test
    @Ignore
    fun readIAXO() {
        val file = File("gdml-source/babyIAXO.gdml")

        val xmlReader = StAXReader(file.inputStream(), "UTF-8")
        val gdml = GDML.format.parse(GDML::class, xmlReader)
        println(gdml.world)
    }
}