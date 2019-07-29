package scientifik.gdml

fun <T : GDMLNode> ref(ref: String): GDMLRef<T> {
    return GDMLRef<T>(ref)
}

fun cubes(): GDML = GDML {

    define {
        position("center")
        position("box_position", x = 25.0, y = 50.0, z = 75.0)
        rotation("Rot1", z = 30) {
            unit = "deg"
        }
    }
    structure {
        val matref = GDMLRef<GDMLMaterial>("G4_AIR")
        val segment = solids.tube("InnerTube", 20, 5.0) {
            rmin = 17
            deltaphi = 60
            aunit = "degree"
        }
        val worldBox = solids.box("World", 200, 200, 200)
        val smallBox = solids.box("World", 30, 30, 30)
        val vol = volume("vol1", matref, segment.ref()) {}
        val circle = volume("Circle", matref, smallBox.ref()) {
            for (i in 0 until 6) {
                physVolume(vol.ref()) {
                    positionref = ref("center")
                    define {
                        rotation("Rot${i}", z = 60 * i) {
                            unit = "deg"
                        }
                    }
                    rotationref = ref("Rot${i}")
                }
            }
        }
        setup.world = volume("world", matref, worldBox.ref()) {
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    for (k in 0 until 3) {
                        define {
                            rotation("Rot$i$j$k", x = i * 120, y = j * 120, z = 120 * k) {
                                unit = "deg"
                            }
                            position("Pos$i$j$k", x = (-50 + i * 50), y = (-50 + j * 50), z = (-50 + k * 50))
                        }
                        physVolume(circle.ref()) {
                            positionref = ref("Pos$i$j$k")
                            rotationref = ref("Rot$i$j$k")
                        }
                    }
                }
            }
        }.ref()
    }
}
