package scientifik.gdml

import kotlinx.serialization.parse
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class GDMLTest {
    @Test
    fun printCubes(){
        val gdml = cubes()
        val string = GDML.format.stringify(gdml)
        println(string)
        val restored: GDML = GDML.format.parse(string)
//        println(restored.toString())
    }

    @Test
    fun testSerialization() {
        val gdml = GDML {
            define {
                rotation("a", y = PI / 6)
                position("pos.b", z = 12)
            }
            solids {
                val myBox = box("myBox", 100, 100, 100)
                val otherBox = box("otherBox", 100, 100, 100)
                union("aUnion", myBox, otherBox) {
                    firstposition = position(32, 0, 0)
                    firstrotation = rotation(y = PI / 4)
                }
            }
        }

        val string = GDML.format.stringify(gdml)
        println(string)
        val restored: GDML = GDML.format.parse(string)
        println(restored.toString())
        assertEquals(gdml.solids.content.first().name, restored.solids.content.first().name)
    }
}