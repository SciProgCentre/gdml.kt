package kscience.gdml

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class GDMLTest {
    @Test
    fun recodeCubes() {
        val gdml = cubes()
        val string = GDML.format.encodeToString(gdml)
        //println(string)
        val restored: GDML = GDML.format.decodeFromString(string)
        assertEquals(gdml.solids.content.size, restored.solids.content.size)
    }

    @Test
    fun testSerialization() {
        val gdml = GDML {
            define {
                rotation("a", y = PI / 6)
                position("pos.b", z = 12)
            }
            solids {
                val myBox = box("myBox", 100.0, 100.0, 100.0)
                val otherBox = box("otherBox", 100.0, 100.0, 100.0)
                union("aUnion", myBox, otherBox) {
                    firstposition = position(32.0, 0.0, 0.0)
                    firstrotation = rotation(y = PI / 4)
                }
            }
        }

        val string = GDML.format.stringify(gdml)
        println(string)
        val restored: GDML = GDML.format.decodeFromString(string)
        println(restored.toString())
        assertEquals(gdml.solids.content[0],restored.solids.content[0])
        assertEquals(gdml.solids.content[1],restored.solids.content[1])
        assertEquals(gdml.solids.content[2],restored.solids.content[2])
        assertEquals(gdml.solids, restored.solids)
    }
}