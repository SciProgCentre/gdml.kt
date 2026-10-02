import space.kscience.gdml.encodeToString
import space.kscience.gdml.script.Gdml
import space.kscience.gdml.script.GdmlScript
import kotlin.script.experimental.host.toScriptSource
import kotlin.test.Test


class TestScriptRendering {

    @Test
    fun testScriptRendering() {
        val script = GdmlScript::class.java.getResourceAsStream("/babyIAXO.gdml.kts")!!
        val gdml = Gdml(script.readAllBytes().decodeToString().toScriptSource())
        println(gdml.encodeToString())
    }
}