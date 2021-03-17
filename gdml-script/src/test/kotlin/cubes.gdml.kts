val center = define.position(name = "center")
structure {
    val air = materials.isotope("G4_AIR")
    val tubeMaterial = materials.element("tubeium")
    val boxMaterial = materials.element("boxium")

    val segment = solids.tube(20, 5.0, "segment") {
        rmin = 17
        deltaphi = 60
        aunit = AUnit.DEG
    }
    val worldBox = solids.box(200, 200, 200, "LargeBox")
    val smallBox = solids.box(30, 30, 30, "smallBox")
    val segmentVolume = volume(tubeMaterial, segment, "segment")
    val circle = volume(boxMaterial, smallBox, "composite") {
        for (i in 0 until 6) {
            physVolume(segmentVolume) {
                positionref = center
                rotation {
                    z = 60 * i
                    unit = AUnit.DEG
                }
            }
        }
    }

    world = volume(air, worldBox, "world") {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                for (k in 0 until 3) {
                    physVolume(circle) {
                        position {
                            x = (-50 + i * 50)
                            y = (-50 + j * 50)
                            z = (-50 + k * 50)
                        }
                        rotation {
                            x = i * 120
                            y = j * 120
                            z = 120 * k
                        }
                    }
                }
            }
        }
    }
}