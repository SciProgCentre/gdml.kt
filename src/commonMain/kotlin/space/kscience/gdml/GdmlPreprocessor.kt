package space.kscience.gdml

import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.XmlDelegatingReader
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.attributes
import kotlin.math.PI

internal class GdmlPreprocessor(
    delegate: XmlReader,
    val evaluate: Map<String, Double>.(String) -> Double,
) : XmlDelegatingReader(delegate) {
    private val variables = hashMapOf("PI" to PI)

    override fun next(): EventType {
        val eventType = super.next()
        //capture all variable definitions
        if (eventType == EventType.START_ELEMENT && (name.getLocalPart() == "variable" || name.getLocalPart() == "constant")) {
            val variableName = attributes.find { it.localName == "name" }?.value
                ?: error("Can't find name attribute in variable or constant")
            val variableValueString = attributes.find { it.localName == "value" }?.value
                ?: error("Can't find value attribute in variable or constant")
            val variableValue = variableValueString.toDoubleOrNull() ?: variables.evaluate(variableValueString)
            variables[variableName] = variableValue
        }
        return eventType
    }

    override fun getAttributeValue(index: Int): String {
        val res = super.getAttributeValue(index)
        val attributeName = getAttributeLocalName(index)
        return if (attributeName in listOf(
                "x",
                "y",
                "z",
                "r",
                "value",
                "rmax",
                "rmax1",
                "rmax2",
                "rmin",
                "rmin1",
                "rmin2"
            )
        ) {
            (res.toDoubleOrNull() ?: variables.evaluate(res)).toString()
        } else {
            res
        }
    }
}