package space.kscience.gdml

import nl.adaptivity.xmlutil.XmlDelegatingWriter
import nl.adaptivity.xmlutil.XmlWriter

public class GdmlPostProcessor(writer: XmlWriter) : XmlDelegatingWriter(writer) {
    override fun startTag(namespace: String?, localName: String, prefix: String?) {
        super.startTag(namespace, localName, prefix)
        if (localName == "gdml") {
            attribute(namespace, "xmlns:xsi", prefix, "http://www.w3.org/2001/XMLSchema-instance")
            attribute(
                namespace,
                "xsi:noNamespaceSchemaLocation",
                prefix,
                "http://service-spi.web.cern.ch/service-spi/app/releases/GDML/schema/gdml.xsd"
            )
        }
    }
}