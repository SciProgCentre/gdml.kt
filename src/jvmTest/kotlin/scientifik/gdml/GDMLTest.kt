package scientifik.gdml

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.Test
import kotlin.math.PI

class GDMLTest {
    @ImplicitReflectionSerializer
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

        val serializer = GDML::class.serializer()

        val string = XML(indent = 4).stringify(serializer, gdml)
        println(string)
        val restored: GDML = XML.parse(string)
    }
}