package space.kscience.gdml.script

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import space.kscience.gdml.encodeToFile
import space.kscience.gdml.encodeToStream
import java.io.File
import java.nio.file.Path
import kotlin.script.experimental.host.toScriptSource

public fun main(args: Array<String>) {
    val parser = ArgParser("gdml-script")
    val input by parser.argument(ArgType.String, description = "Input file path")
    val output by parser.argument(ArgType.String, description = "Output file path").optional()
    parser.parse(args)

    require(input.endsWith("gdml.kts"))

    val inputFile: File = File(input)
    val gdml = Gdml(inputFile.toScriptSource())

    output?.let {
        val path = Path.of(output)
        gdml.encodeToFile(path)
    } ?: gdml.encodeToStream(System.out)


}