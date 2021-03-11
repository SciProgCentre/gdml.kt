package space.kscience.gdml

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class GdmlTest {
    @Test
    fun recodeCubes() {
        val gdml = cubes()
        val string = gdml.encodeToString()
        //println(string)
        val restored: Gdml = Gdml.format.decodeFromString(string)
        assertEquals(gdml.solids.content.size, restored.solids.content.size)
    }

    @Test
    fun testSerialization() {
        val gdml = Gdml {
            define {
                rotation("a", y = PI / 6)
                position("pos.b", z = 12)
            }
            solids {
                val myBox = box("myBox", 100.0, 100.0, 100.0)
                val otherBox = box("otherBox", 100.0, 100.0, 100.0)
                union("aUnion", myBox, otherBox) {
                    firstposition = GdmlPosition(x = 32.0, y = 0.0, z = 0.0)
                    firstrotation = GdmlRotation(y = PI / 4)
                }
            }
        }

        val string = Gdml.format.encodeToString(gdml)
        println(string)
        val restored: Gdml = Gdml.format.decodeFromString(string)
        println(restored.toString())
        assertEquals(gdml.solids.content[0], restored.solids.content[0])
        assertEquals(gdml.solids.content[1], restored.solids.content[1])
        assertEquals(gdml.solids.content[2], restored.solids.content[2])
        assertEquals(gdml.solids, restored.solids)
    }

    @Test
    fun testSubtraction() {
        val gdml = Gdml {
            val cube = solids.box("theBox", 100, 100, 100)
            val orb = solids.orb("theOrb", 100)

            val subtract = solids.subtraction("sub", cube, orb) {
                position = GdmlPosition(x = 100, y = 0, z = 0)
            }
        }
        println(gdml.encodeToString())
    }
}