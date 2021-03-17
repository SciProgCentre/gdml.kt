package space.kscience.gdml

import kotlinx.browser.window
import kotlin.test.Test


class BMNTest {

    @Test
    fun testRead() {
        println("Started")
        window.fetch("https://drive.google.com/open?id=1w5e7fILMN83JGgB8WANJUYm8OW2s0WVO").then {
            println("Fetched!")
            val string = it.body as String
            val xml = Gdml.decodeFromString(string)
            println(xml.world)
        }.catch {
            println("Fetch failed")
            println(it.message)
        }
    }
}