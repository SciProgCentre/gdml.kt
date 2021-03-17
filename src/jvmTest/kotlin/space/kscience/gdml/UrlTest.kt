package space.kscience.gdml

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UrlTest {
    @Test
    fun decodeFromUrl() {
        val gdml =
            Gdml.decodeFromUrl("https://raw.githubusercontent.com/mipt-npm/gdml.kt/master/gdml-source/cubes.gdml")
        assertNotNull(gdml.getSolid("segment"))
        //println(gdml)
    }

    @Test
    fun loadMaterials() {
        val gdml = Gdml {
            loadMaterialsFromUrl("https://raw.githubusercontent.com/rest-for-physics/materials/2e1d7b0246deecdff70e3e51432e9c55f8862b99/NIST.xml")
        }

        assertEquals(3.0, gdml.getMaterial<GdmlIsotope>("He3")?.n)
        //println(gdml)
    }
}