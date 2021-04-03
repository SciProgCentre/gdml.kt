package space.kscience.gdml.builder

import space.kscience.gdml.*
import kotlin.reflect.typeOf

@GdmlApi
public interface GdmlRegistry: GdmlSolidRegistry {
    public fun <R : GdmlDefine> registerDefine(item: R): GdmlRef<R>
    public fun <R : GdmlMaterial> registerMaterial(item: R): GdmlRef<R>
    public fun <R : GdmlGroup> registerGroup(item: R): GdmlRef<R>

    public val defaultMaterial: GdmlRef<GdmlMaterial>
}

public inline fun <reified R : GdmlNode> GdmlRegistry.resolveName(suffix: String?): String =
    resolveName(suffix, typeOf<R>())


public class GdmlBuilder(public val registry: GdmlRegistry, public val parent: GdmlGroup) {

    public inline fun <reified T : GdmlSolid> solid(
        modifier: GdmlModifier,
        namePrefix: String?,
        builder: (String) -> T,
    ): GdmlPhysVolume {
        val type = typeOf<T>()
        val name = registry.resolveName(namePrefix?.let{"$namePrefix-solid"}, type)
        val solid = builder(name)
        val solidRef = registry.registerSolid(solid)
        val volume: GdmlVolume = GdmlVolume(
            registry.resolveName(namePrefix?.let{"$namePrefix-volume"}, type),
            modifier.get() ?: registry.defaultMaterial,
            solidRef
        )
        val volumeRef = registry.registerGroup(volume)
        return parent.physVolume(volumeRef, registry.resolveName(namePrefix?.let{"$namePrefix-physVol"}, type)) {
            modifier.get<GdmlPosition>()?.let { position = it }
            modifier.get<GdmlRef<GdmlPosition>>()?.let { positionref = it }
            modifier.get<GdmlRotation>()?.let { rotation = it }
            modifier.get<GdmlRef<GdmlRotation>>()?.let { rotationref = it }
            modifier.get<GdmlScale>()?.let { scale = it }
            modifier.get<GdmlRef<GdmlScale>>()?.let { scaleref = it }
        }
    }

}

//@GdmlApi
//public inline fun GdmlBuilder.assembly(name: String? = null, block: GdmlAssembly.() -> Unit): GdmlRef<GdmlAssembly>{
//    val assembly = GdmlAssembly(resolveName<GdmlAssembly>(name)).apply(block)
//    return registerGroup(assembly)
//}
//
//@GdmlApi
//public class GdmlBuilderImpl(private val gdml: Gdml, private val prefix: String) {
//
//    @PublishedApi
//    internal fun <R : GdmlDefine> registerDefine(item: R): GdmlRef<R> = gdml.define.register(item)
//
//    @PublishedApi
//    internal fun <R : GdmlSolid> registerSolid(item: R): GdmlRef<R> = gdml.solids.register(item)
//
//    @PublishedApi
//    internal fun <R : GdmlGroup> registerGroup(item: R): GdmlRef<R> = gdml.structure.register(item)
//
//    /**
//     * A counter used for auto-naming
//     */
//    private var autoNameCounter = 0
//
//    @OptIn(ExperimentalSerializationApi::class)
//    @PublishedApi
//    internal fun resolveName(suffix: String?, type: KType): String {
//        val actualSuffix: String = suffix ?: "${serializer(type).descriptor.serialName}-${autoNameCounter++}"
//        return "$prefix.$actualSuffix"
//    }
//
//    @PublishedApi
//    internal inline fun <reified R : GdmlNode> resolveName(suffix: String?): String = resolveName(suffix, typeOf<R>())
//
//}