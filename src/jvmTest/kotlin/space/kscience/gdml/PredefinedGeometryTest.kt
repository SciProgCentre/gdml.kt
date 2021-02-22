package space.kscience.gdml

import org.junit.jupiter.api.Test
import java.io.File

class PredefinedGeometryTest {

//    @OptIn(InternalSerializationApi::class)
//    @Test
//    fun printChildren() {
//        println(
//            GdmlDefine::class.sealedSubclasses.joinToString(
//                prefix = "[\"",
//                separator = "\", \"",
//                postfix = "\"]"
//            ) { it.serializer().descriptor.serialName })
//
//        println(
//            GdmlMaterial::class.sealedSubclasses.joinToString(
//                prefix = "[\"",
//                separator = "\", \"",
//                postfix = "\"]"
//            ) { it.serializer().descriptor.serialName })
//
//        println(
//            GdmlSolid::class.sealedSubclasses.joinToString(
//                prefix = "[\"",
//                separator = "\", \"",
//                postfix = "\"]"
//            ) { it.serializer().descriptor.serialName })
//    }

    @Test
    fun testReadBMN() {
        val file = File("gdml-source/BM@N.gdml")

        val gdml = Gdml.decodeFromStream(file.inputStream())
        println(gdml.world)
        val ref = GdmlRef<GdmlAssembly>("Magnet")
        val magnet = ref.resolve(gdml)!!
        println(magnet)
    }

    @Test
    fun testReadCubes() {
        val file = File("gdml-source/cubes.gdml")

        val gdml = Gdml.decodeFromStream(file.inputStream())
        println(gdml.world)
    }

    @Test
    fun readIAXO() {
        val file = File("gdml-source/babyIAXO.gdml")
        val gdml = Gdml.decodeFromStream(file.inputStream(),true)
        println(gdml.world)
    }
}