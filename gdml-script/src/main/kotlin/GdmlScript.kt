package space.kscience.gdml.script

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.kscience.gdml.Gdml
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

@KotlinScript(
    fileExtension = "gdml.kts",
    compilationConfiguration = GdmlScriptCompilationConfiguration::class
)
abstract class GdmlScript

object GdmlScriptCompilationConfiguration : ScriptCompilationConfiguration({
    baseClass(GdmlScript::class)
    implicitReceivers(Gdml::class)
    defaultImports(
        "kotlin.math.*",
        "space.kscience.gdml.*"
    )
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
        compilerOptions.append("-jvm-target", Runtime.version().feature().toString())
    }
    hostConfiguration(defaultJvmScriptingHostConfiguration)
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})

internal fun Gdml(
    source: SourceCode,
    logger: Logger = LoggerFactory.getLogger("scripting"),
): Gdml {

    return Gdml {
        val flow = this
        val evaluationConfiguration = ScriptEvaluationConfiguration {
            implicitReceivers(flow)
        }
        BasicJvmScriptingHost().eval(source, GdmlScriptCompilationConfiguration, evaluationConfiguration).onFailure {
            it.reports.forEach { scriptDiagnostic ->
                when (scriptDiagnostic.severity) {
                    ScriptDiagnostic.Severity.FATAL, ScriptDiagnostic.Severity.ERROR -> {
                        logger.error(scriptDiagnostic.toString(), scriptDiagnostic.exception)
                        error(scriptDiagnostic.toString())
                    }
                    ScriptDiagnostic.Severity.WARNING -> logger.warn(scriptDiagnostic.toString())
                    ScriptDiagnostic.Severity.INFO -> logger.info(scriptDiagnostic.toString())
                    ScriptDiagnostic.Severity.DEBUG -> logger.debug(scriptDiagnostic.toString())
                }
            }
        }
    }
}
