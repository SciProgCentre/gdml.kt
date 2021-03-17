package space.kscience.gdml

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class GdmlTest {
    @Test
    fun recodeCubes() {
        val string = GdmlShowCase.cubes.encodeToString()
        //println(string)
        val restored: Gdml = Gdml.xmlFormat.decodeFromString(string)
        assertEquals(GdmlShowCase.cubes.solids.content.size, restored.solids.content.size)
    }

    @Test
    fun recodeIaxo() {
        val string = GdmlShowCase.babyIaxo.encodeToString()
        println(string)
        val restored: Gdml = Gdml.xmlFormat.decodeFromString(string)
        assertEquals(GdmlShowCase.babyIaxo.solids.content.size, restored.solids.content.size)
    }

    @Test
    fun serialization() {
        val gdml = Gdml {
            define {
                rotation(name = "a", y = PI / 6)
                position(name = "pos.b", z = 12)
            }
            solids {
                val myBox = box(100.0, 100.0, 100.0, "myBox")
                val otherBox = box(100.0, 100.0, 100.0, "otherBox")
                union(myBox, otherBox, "aUnion") {
                    firstposition = GdmlPosition(x = 32.0, y = 0.0, z = 0.0)
                    firstrotation = GdmlRotation(y = PI / 4)
                }
            }
        }

        val string = Gdml.xmlFormat.encodeToString(gdml)
        println(string)
        val restored: Gdml = Gdml.xmlFormat.decodeFromString(string)
        println(restored.toString())
        assertEquals(gdml.solids.content[0], restored.solids.content[0])
        assertEquals(gdml.solids.content[1], restored.solids.content[1])
        assertEquals(gdml.solids.content[2], restored.solids.content[2])
        assertEquals(gdml.solids, restored.solids)
    }

    @Test
    fun subtraction() {
        val gdml = Gdml {
            val cube = solids.box(100, 100, 100, "theBox")
            val orb = solids.orb(100, "theOrb")

            val subtract = solids.subtraction(cube, orb, "sub") {
                position = GdmlPosition(x = 100, y = 0, z = 0)
            }
        }
        println(gdml.encodeToString())
    }

    @Test
    fun autoNaming() {
        val gdml = Gdml {
            val cube1 = solids.box(100, 100, 100)
            val cube2 = solids.box(100, 100, 100)
            val cube3 = solids.box(100, 100, 100)
            val air = materials.isotope("G4_AIR")
            val volume1 = structure.volume(air, cube1)
            val volume2 = structure.volume(air, cube2)
            val volume3 = structure.volume(air, cube3)
            world {
                physVolume(volume1)
                physVolume(volume2)
                physVolume(volume3)
            }
        }
        println(gdml)
        assertEquals(3, gdml.world.resolve(gdml)?.physVolumes?.size)
    }

    @Test
    fun rotationGroup() {
        val gdml = Gdml {
            val cube = solids.box(100, 100, 100)
            val air = materials.isotope("G4_AIR")
            structure {
                val cubeVolume = volume(air, cube)
                val group = assembly{
                    repeat(5) { index ->
                        physVolume(cubeVolume) {
                            position { z = index * 100 }
                        }
                    }
                }
                world = assembly("rotated") {
                    physVolume(group) {
                        rotation {
                            x = PI / 4
                        }
                    }
                }
            }
        }
        println(gdml)
    }
}