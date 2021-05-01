package space.kscience.gdml

import kotlinx.browser.window
import kotlin.test.Ignore
import kotlin.test.Test


class GdmlJsTest {

    @Test
    @Ignore
    fun testRead() = window.fetch(
        "https://gist.githubusercontent.com/altavir/671de3f0300309dcdc02a379d0aae03c/raw/cf637102a8d1dd651db8ad0b11ed4edf434b4368/BM@N.gdml"
    ).then {
        println("Fetched!")
        val string = it.body as String
        val xml = Gdml.decodeFromString(string)
        println(xml.world)
    }.catch {
        println("Fetch failed")
        println(it.message)
    }

}