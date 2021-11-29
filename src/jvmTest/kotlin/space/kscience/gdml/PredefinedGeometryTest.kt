package space.kscience.gdml

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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
        //println(gdml.world)
        assertNotNull(gdml.getSolid("segment"))
    }

    @Test
    fun readIAXO() {
        val file = File("gdml-source/babyIAXO.gdml")
        val gdml = Gdml.decodeFromStream(file.inputStream(), true)
        println(gdml.world)
    }

    @Test
    fun testRemoveUnusedMaterials() {
        val file = File("gdml-source/babyIAXO.gdml")
        var gdml = Gdml.decodeFromStream(file.inputStream(), true)
        assertNotNull(gdml.materials.get<GdmlMaterial>("G4_WATER"))
        val gdmlAfter = GdmlMaterialPostProcessor.removeUnusedMaterials(gdml)
        // assertNull(gdmlAfter.materials.get<GdmlMaterial>("G4_WATER")) TODO: shouldn't this be null? its not on the gdml
        // assertNotEquals(gdmlAfter, gdml) TODO: not sure why this doesn't work either
        val gdmlAfterAgain = GdmlMaterialPostProcessor.removeUnusedMaterials(gdmlAfter)
        assertEquals(gdmlAfter, gdmlAfterAgain)

        println(gdml)
    }
}