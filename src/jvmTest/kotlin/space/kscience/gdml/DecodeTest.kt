package space.kscience.gdml

import kotlin.test.Test

class DecodeTest {
    @Test
    fun decodeFromUrl(){
        val gdml = Gdml.decodeFromUrl("https://raw.githubusercontent.com/mipt-npm/gdml.kt/master/gdml-source/cubes.gdml")
        println(gdml)
    }
}