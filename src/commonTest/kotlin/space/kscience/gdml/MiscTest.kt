package space.kscience.gdml

import kotlin.test.Test
import kotlin.test.assertFails

class MiscTest {
    @Test
    fun nameValidatin(){
        "someName".validateNCName()
        assertFails { "some name".validateNCName() }
        assertFails { "some[name]".validateNCName() }
        assertFails { "2someName".validateNCName() }
    }
}