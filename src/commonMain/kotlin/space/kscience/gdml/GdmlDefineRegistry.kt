package space.kscience.gdml


public interface GdmlDefineRegistry: GdmlNameGenerator {
    public fun <R : GdmlDefine> registerDefine(item: R): GdmlRef<R>
}

@GdmlApi
public inline fun GdmlDefineRegistry.position(
    x: Number = 0f,
    y: Number = 0f,
    z: Number = 0f,
    name: String? = null,
    block: GdmlPosition.() -> Unit = {},
): GdmlRef<GdmlPosition> {
    val position = GdmlPosition(generateName<GdmlPosition>(name)).apply(block).apply {
        this.name = generateName<GdmlPosition>(name)
        this.x = x
        this.y = y
        this.z = z
    }

    return registerDefine(position)
}

@GdmlApi
public inline fun GdmlDefineRegistry.rotation(
    x: Number = 0f,
    y: Number = 0f,
    z: Number = 0f,
    name: String? = null,
    block: GdmlRotation.() -> Unit = {},
): GdmlRef<GdmlRotation> {
    val rotation = GdmlRotation(generateName<GdmlRotation>(name)).apply(block).apply {
        this.name = generateName<GdmlRotation>(name)
        this.x = x
        this.y = y
        this.z = z
    }

    return registerDefine(rotation)
}
