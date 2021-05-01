package space.kscience.gdml

import kotlin.test.Test
import kotlin.test.assertEquals

class PreprocessorTest {

    @Test
    fun testGdmlVariable() {
        val gdmlString = """
            <?xml version="1.0" encoding="UTF-8" standalone="no" ?>
            <gdml xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://service-spi.web.cern.ch/service-spi/app/releases/Gdml/schema/gdml.xsd">
                <define>
                    <variable name="chamberLength" value="30" />
                    <variable name="chamberRadius" value="50" />
                    <variable name="mylarThickness" value="0.004" />
                    <variable name="mylarCathodePositionZ" value="chamberLength/2.-mylarThickness/2." />
                    <position name="mylarCathodePosition" unit="mm" x="0" y="0" z="chamberLength/2.-mylarThickness/2." />
                </define>
            </gdml>
        """.trimIndent()

        val gdml = Gdml.decodeFromString(gdmlString,true)
        assertEquals(14.998, gdml.getDefine<GdmlPosition>("mylarCathodePosition")?.z)
    }
}