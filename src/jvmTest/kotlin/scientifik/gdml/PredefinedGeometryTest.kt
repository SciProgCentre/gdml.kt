package scientifik.gdml

import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.StAXReader
import org.junit.Test
import scientifik.gdml.*
import java.io.File

class PredefinedGeometryTest {

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
}