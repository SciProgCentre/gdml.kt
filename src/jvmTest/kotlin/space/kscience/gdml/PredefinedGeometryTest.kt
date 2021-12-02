package space.kscience.gdml

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

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
        val gdml = Gdml.decodeFromStream(file.inputStream(), true)
        val materialToRemove = gdml.materials.get<GdmlMaterial>("G4_WATER")
        assertNotNull(materialToRemove)
        assert(gdml.materials.content.contains(materialToRemove))
        val gdmlMaterialsRemoved = gdml.removeUnusedMaterials()
        assert(!gdmlMaterialsRemoved.materials.content.contains(materialToRemove))
        val gdmlMaterialsRemovedAgain = gdmlMaterialsRemoved.removeUnusedMaterials()
        assertEquals(gdmlMaterialsRemoved, gdmlMaterialsRemovedAgain)

        println(gdml)
    }
}