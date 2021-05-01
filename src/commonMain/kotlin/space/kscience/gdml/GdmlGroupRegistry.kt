package space.kscience.gdml

@GdmlApi
public interface GdmlGroupRegistry : GdmlNameGenerator {
    public fun <R : GdmlGroup> registerGroup(item: R): GdmlRef<R>
}

@GdmlApi
public inline fun GdmlGroupRegistry.volume(
    materialref: GdmlRef<GdmlMaterial>,
    solidref: GdmlRef<GdmlSolid>,
    name: String? = null,
    block: GdmlVolume.() -> Unit = {},
): GdmlRef<GdmlVolume> {
    val res = GdmlVolume(generateName<GdmlVolume>(name), materialref, solidref).apply(block)
    return registerGroup(res)
}

@GdmlApi
public inline fun GdmlGroupRegistry.assembly(
    name: String? = null,
    block: GdmlAssembly.() -> Unit = {},
): GdmlRef<GdmlAssembly> {
    val res = GdmlAssembly(generateName<GdmlAssembly>(name)).apply(block)
    return registerGroup(res)
}