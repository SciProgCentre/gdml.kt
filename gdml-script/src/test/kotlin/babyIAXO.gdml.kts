import space.kscience.gdml.*

// geometry variables
val worldSize = 1000
// chamber
val chamberHeight = 30 // length of the chamber
val chamberDiameter = 102 // inner diameter of the copper chamber
val chamberOuterSquareSide = 134 // chamber has a square footprint
val chamberBackplateThickness = 15 // thickness of the backplate of the chamber
// teflon disk
val cathodeTeflonDiskHoleRadius = 15
val cathodeTeflonDiskThickness = 5
val cathodeCopperSupportOuterRadius = 45
val cathodeCopperSupportInnerRadius = 8.5
val cathodeCopperSupportThickness = 1
// mylar cathode
val mylarCathodeThickness = 0.004
// patern
val cathodePatternLineWidth = 0.3
val cathodePatternDiskRadius = 4.25
// readout
val chamberTeflonWallThickness = 1
val readoutKaptonThickness = 0.5
val readoutCopperThickness = 0.2
val readoutPlaneSide = 60
// pipe
val detectorPipeChamberFlangeThickness = 14
val detectorPipeChamberFlangeRadius = chamberOuterSquareSide / 2
val detectorPipeTelescopeFlangeThickness = 18
val detectorPipeTelescopeFlangeRadius = 150 / 2
val detectorPipeTotalLength = 491
val detectorPipeSection2of2Length = 150 - detectorPipeTelescopeFlangeThickness
val detectorPipeSection1of2Length =
    detectorPipeTotalLength - detectorPipeTelescopeFlangeThickness - detectorPipeChamberFlangeThickness - detectorPipeSection2of2Length
val detectorPipeOuterRadius1 = 92 / 2
val detectorPipeOuterRadius2 = 108 / 2
val detectorPipeUnion1Z = detectorPipeChamberFlangeThickness / 2 + detectorPipeSection1of2Length / 2
val detectorPipeUnion2Z =
    detectorPipeUnion1Z + detectorPipeSection1of2Length / 2 + detectorPipeSection2of2Length / 2
val detectorPipeUnion3Z =
    detectorPipeUnion2Z + detectorPipeSection2of2Length / 2 + detectorPipeTelescopeFlangeThickness / 2
val detectorPipeInsideSection1of3Radius = 43 / 2
val detectorPipeInsideSection2of3Radius = 68 / 2
val detectorPipeInsideSection3of3Radius = 85 / 2
val detectorPipeInsideSectionTelescopeRadius = 108 / 2
val detectorPipeInsideCone1of3Length = 21.65
val detectorPipeInsideCone2of3Length = 14.72
val detectorPipeInsideCone3of3Length = 9
val detectorPipeInsideSection3of3Length = 115 - detectorPipeInsideCone3of3Length
val detectorPipeInsideSection2of3Length =
    290 - detectorPipeInsideSection3of3Length - detectorPipeInsideCone3of3Length - detectorPipeInsideCone2of3Length
val detectorPipeInsideSection1of3Length = 201 - detectorPipeInsideCone1of3Length
val detectorPipeInsideUnion1Z = detectorPipeInsideSection1of3Length / 2 + detectorPipeInsideCone1of3Length / 2
val detectorPipeInsideUnion2Z =
    detectorPipeInsideUnion1Z + detectorPipeInsideCone1of3Length / 2 + detectorPipeInsideSection2of3Length / 2
val detectorPipeInsideUnion3Z =
    detectorPipeInsideUnion2Z + detectorPipeInsideSection2of3Length / 2 + detectorPipeInsideCone2of3Length / 2
val detectorPipeInsideUnion4Z =
    detectorPipeInsideUnion3Z + detectorPipeInsideCone2of3Length / 2 + detectorPipeInsideSection3of3Length / 2
val detectorPipeInsideUnion5Z =
    detectorPipeInsideUnion4Z + detectorPipeInsideSection3of3Length / 2 + detectorPipeInsideCone3of3Length / 2
val detectorPipeFillingOffsetWithPipe =
    detectorPipeInsideSection1of3Length / 2 - detectorPipeChamberFlangeThickness / 2
val detectorPipeZinWorld =
    detectorPipeChamberFlangeThickness / 2 + chamberHeight / 2 + cathodeTeflonDiskThickness
// shielding
val leadBoxSizeXY = 590
val leadBoxSizeZ = 540
val leadBoxShaftShortSideX = 194
val leadBoxShaftShortSideY = 170
val leadBoxShaftLongSide = 340
val detectorToShieldingSeparation = -60
val shieldingOffset =
    detectorToShieldingSeparation + chamberHeight / 2 + readoutKaptonThickness + chamberBackplateThickness

structure {
    val worldMaterial = materials.composite("G4_AIR")
    val worldBox = solids.box("worldBox", worldSize, worldSize, worldSize)

    val shieldingMaterial = materials.composite("G4_Pb")
    val scintillatorMaterial = materials.composite("BC408")
    val captureMaterial = materials.composite("G4_Cd")

    // chamber
    val copperMaterial = materials.composite("G4_Cu")
    val chamberSolidBase =
        solids.box("chamberSolidBase", chamberOuterSquareSide, chamberOuterSquareSide, chamberHeight)
    val chamberSolidHole = solids.tube("chamberSolidHole", chamberDiameter / 2, chamberHeight)
    val chamberSolid = solids.subtraction("chamberSolid", chamberSolidBase, chamberSolidHole)
    val chamberBodyVolume = volume("chamberBodyVolume", copperMaterial, chamberSolid)
    val chamberBackplateSolid = solids.box("chamberBackplateSolid",
        chamberOuterSquareSide,
        chamberOuterSquareSide,
        chamberBackplateThickness
    )
    val chamberBackplateVolume = volume("chamberBackplateVolume", copperMaterial, chamberBackplateSolid)
    // chamber teflon walls
    val teflonMaterial = materials.composite("G4_TEFLON")
    val chamberTeflonWallSolid = solids.tube("chamberTeflonWallSolid", chamberDiameter / 2, chamberHeight) {
        rmin = chamberDiameter / 2 - chamberTeflonWallThickness
    }
    val chamberTeflonWallVolume = volume("chamberTeflonWallVolume", teflonMaterial, chamberTeflonWallSolid)
    // cathode
    val cathodeCopperDiskMaterial = materials.composite("G4_Cu")
    val cathodeWindowMaterial = materials.composite("G4_MYLAR")

    val cathodeTeflonDiskSolidBase =
        solids.tube("cathodeTeflonDiskSolidBase", chamberOuterSquareSide / 2, cathodeTeflonDiskThickness) {
            rmin = cathodeTeflonDiskHoleRadius
        }
    val cathodeCopperDiskSolid =
        solids.tube("cathodeCopperDiskSolid", cathodeCopperSupportOuterRadius, cathodeCopperSupportThickness) {
            rmin = cathodeCopperSupportInnerRadius
        }

    val cathodeTeflonDiskSolid =
        solids.subtraction("cathodeTeflonDiskSolid", cathodeTeflonDiskSolidBase, cathodeCopperDiskSolid)
    val cathodeTeflonDiskVolume = volume("cathodeTeflonDiskVolume", teflonMaterial, cathodeTeflonDiskSolid)

    val cathodeWindowSolid =
        solids.tube("cathodeWindowSolid", cathodeTeflonDiskHoleRadius, mylarCathodeThickness)
    val cathodeWindowVolume = volume("cathodeWindowVolume", cathodeWindowMaterial, cathodeWindowSolid)

    val vacuumMaterial = materials.composite("G4_Galactic")
    val cathodeFillingSolidBase =
        solids.tube("cathodeFillingSolidBase", cathodeTeflonDiskHoleRadius, cathodeTeflonDiskThickness)

    val cathodeFillingSolid =
        solids.subtraction("cathodeFillingSolid", cathodeFillingSolidBase, cathodeCopperDiskSolid) {

            position = GdmlPosition(unit = LUnit.MM, z = chamberHeight / 2 - mylarCathodeThickness / 2)
        }
    val cathodeFillingVolume = volume("cathodeFillingVolume", vacuumMaterial, cathodeFillingSolid)

    // pattern
    val cathodePatternLineAux = solids.box(
        "cathodePatternLineAux",
        cathodePatternLineWidth,
        cathodeCopperSupportInnerRadius * 2,
        cathodeCopperSupportThickness
    )
    val cathodePatternCentralHole = solids.tube(
        "cathodePatternCentralHole",
        cathodePatternDiskRadius - 0 * cathodePatternLineWidth,
        cathodeCopperSupportThickness * 1.1
    )
    val cathodePatternLine =
        solids.subtraction("cathodePatternLine", cathodePatternLineAux, cathodePatternCentralHole)

    val cathodePatternDisk = solids.tube(
        "cathodePatternDisk",
        cathodePatternDiskRadius,
        cathodeCopperSupportThickness
    ) { rmin = cathodePatternDiskRadius - cathodePatternLineWidth }


    val cathodeCopperDiskSolidAux0 =
        solids.union("cathodeCopperDiskSolidAux0", cathodeCopperDiskSolid, cathodePatternLine) {
            rotation = GdmlRotation(x = 0, y = 0, z = 0)
        }
    val cathodeCopperDiskSolidAux1 =
        solids.union("cathodeCopperDiskSolidAux1", cathodeCopperDiskSolidAux0, cathodePatternLine) {
            rotation = GdmlRotation(x = 0, y = 0, z = 45)
        }
    val cathodeCopperDiskSolidAux2 =
        solids.union("cathodeCopperDiskSolidAux2", cathodeCopperDiskSolidAux1, cathodePatternLine) {
            rotation = GdmlRotation(x = 0, y = 0, z = 90)
        }
    val cathodeCopperDiskSolidAux3 =
        solids.union("cathodeCopperDiskSolidAux3", cathodeCopperDiskSolidAux2, cathodePatternLine) {
            rotation = GdmlRotation(x = 0, y = 0, z = 135)
        }

    val cathodeCopperDiskFinal =
        solids.union("cathodeCopperDiskFinal", cathodeCopperDiskSolidAux3, cathodePatternDisk)


    val cathodeCopperDiskVolume =
        volume("cathodeCopperDiskFinal", cathodeCopperDiskMaterial, cathodeCopperDiskFinal)

    val gasSolidOriginal = solids.tube(
        "gasSolidOriginal",
        chamberDiameter / 2 - chamberTeflonWallThickness,
        chamberHeight
    )

    val kaptonReadoutMaterial = materials.composite("G4_KAPTON")
    val kaptonReadoutSolid =
        solids.box("kaptonReadoutSolid", chamberOuterSquareSide, chamberOuterSquareSide, readoutKaptonThickness)
    val kaptonReadoutVolume = volume("kaptonReadoutVolume", kaptonReadoutMaterial, kaptonReadoutSolid)

    val copperReadoutSolid =
        solids.box("copperReadoutSolid", readoutPlaneSide, readoutPlaneSide, readoutCopperThickness)
    val copperReadoutVolume = volume("copperReadoutVolume", copperMaterial, copperReadoutSolid)

    val gasSolidAux =
        solids.subtraction("gasSolidAux", gasSolidOriginal, copperReadoutSolid) {
            position = GdmlPosition(unit = LUnit.MM, z = -chamberHeight / 2 + readoutCopperThickness / 2)
        }

    val gasMaterial = materials.composite("G4_Ar")
    val gasSolid =
        solids.subtraction("gasSolid", gasSolidAux, cathodeWindowSolid) {
            position = GdmlPosition(unit = LUnit.MM, z = chamberHeight / 2 - mylarCathodeThickness / 2)
            rotation = GdmlRotation(z = 45)
        }
    val gasVolume = volume("gasVolume", gasMaterial, gasSolid)

    val leadBoxSolid = solids.box("leadBoxSolid", leadBoxSizeXY, leadBoxSizeXY, leadBoxSizeZ)
    val leadBoxShaftSolid = solids.box(
        "leadBoxShaftSolid",
        leadBoxShaftShortSideX,
        leadBoxShaftShortSideY,
        leadBoxShaftLongSide
    )
    val leadBoxWithShaftSolid = solids.subtraction("leadBoxWithShaftSolid", leadBoxSolid, leadBoxShaftSolid) {

        position = GdmlPosition(unit = LUnit.MM, z = leadBoxSizeZ / 2 - leadBoxShaftLongSide / 2)
    }
    val leadShieldingVolume = volume("ShieldingVolume", shieldingMaterial, leadBoxWithShaftSolid)

    val detectorPipeChamberFlangeSolid = solids.tube("detectorPipeChamberFlangeSolid",
        detectorPipeChamberFlangeRadius,
        detectorPipeChamberFlangeThickness)
    val detectorPipeTelescopeFlangeSolid = solids.tube("detectorPipeTelescopeFlangeSolid",
        detectorPipeTelescopeFlangeRadius,
        detectorPipeTelescopeFlangeThickness)
    val detectorPipeSection1of2Solid =
        solids.tube("detectorPipeSection1of2Solid", detectorPipeOuterRadius1, detectorPipeSection1of2Length)
    val detectorPipeSection2of2Solid =
        solids.tube("detectorPipeSection2of2Solid", detectorPipeOuterRadius2, detectorPipeSection2of2Length)
    val detectorPipeAux1 =
        solids.union("detectorPipeAux1", detectorPipeChamberFlangeSolid, detectorPipeSection1of2Solid) {

            position = GdmlPosition(unit = LUnit.MM, z = detectorPipeUnion1Z)
        }
    val detectorPipeAux2 = solids.union("detectorPipeAux2", detectorPipeAux1, detectorPipeSection2of2Solid) {

        position = GdmlPosition(unit = LUnit.MM, z = detectorPipeUnion2Z)
    }
    val detectorPipeNotEmpty =
        solids.union("detectorPipeNotEmpty", detectorPipeAux2, detectorPipeTelescopeFlangeSolid) {

            position = GdmlPosition(unit = LUnit.MM, z = detectorPipeUnion3Z)
        }

    val detectorPipeInside1of3Solid = solids.tube("detectorPipeInside1of3Solid",
        detectorPipeInsideSection1of3Radius,
        detectorPipeInsideSection1of3Length)
    val detectorPipeInside2of3Solid = solids.tube("detectorPipeInside2of3Solid",
        detectorPipeInsideSection2of3Radius,
        detectorPipeInsideSection2of3Length)
    val detectorPipeInside3of3Solid = solids.tube("detectorPipeInside3of3Solid",
        detectorPipeInsideSection3of3Radius,
        detectorPipeInsideSection3of3Length)

    val detectorPipeInsideCone1of3Solid =
        solids.cone("detectorPipeInsideCone1of3Solid", detectorPipeInsideCone1of3Length,
            detectorPipeInsideSection1of3Radius, detectorPipeInsideSection2of3Radius)
    val detectorPipeInsideCone2of3Solid =
        solids.cone("detectorPipeInsideCone2of3Solid", detectorPipeInsideCone2of3Length,
            detectorPipeInsideSection2of3Radius, detectorPipeInsideSection3of3Radius)
    val detectorPipeInsideCone3of3Solid =
        solids.cone("detectorPipeInsideCone3of3Solid", detectorPipeInsideCone3of3Length,
            detectorPipeInsideSection3of3Radius, detectorPipeInsideSectionTelescopeRadius)

    val detectorPipeInsideAux1 =
        solids.union("detectorPipeInsideAux1", detectorPipeInside1of3Solid, detectorPipeInsideCone1of3Solid) {
            position = GdmlPosition(unit = LUnit.MM, z = detectorPipeInsideUnion1Z)
        }
    val detectorPipeInsideAux2 =
        solids.union("detectorPipeInsideAux2", detectorPipeInsideAux1, detectorPipeInside2of3Solid) {
            position = GdmlPosition(unit = LUnit.MM, z = detectorPipeInsideUnion2Z)
        }
    val detectorPipeInsideAux3 =
        solids.union("detectorPipeInsideAux3", detectorPipeInsideAux2, detectorPipeInsideCone2of3Solid) {
            position = GdmlPosition(unit = LUnit.MM, z = detectorPipeInsideUnion3Z)
        }
    val detectorPipeInsideAux4 =
        solids.union("detectorPipeInsideAux4", detectorPipeInsideAux3, detectorPipeInside3of3Solid) {
            position = GdmlPosition(unit = LUnit.MM, z = detectorPipeInsideUnion4Z)
        }
    val detectorPipeInside =
        solids.union("detectorPipeInside", detectorPipeInsideAux4, detectorPipeInsideCone3of3Solid) {
            position = GdmlPosition(unit = LUnit.MM, z = detectorPipeInsideUnion5Z)
        }
    val detectorPipeSolid = solids.subtraction("detectorPipeSolid", detectorPipeNotEmpty, detectorPipeInside) {
        position = GdmlPosition(
            unit = LUnit.MM,
            z = detectorPipeInsideSection1of3Length / 2 - detectorPipeChamberFlangeThickness / 2
        )
    }
    val detectorPipeVolume = volume("detectorPipeVolume", copperMaterial, detectorPipeSolid)
    val detectorPipeFillingVolume = volume("detectorPipeFillingVolume", vacuumMaterial, detectorPipeInside)

    // world setup
    world = volume("world", worldMaterial, worldBox) {
        physVolume(gasVolume) {
            name = "gas"
        }
        physVolume(kaptonReadoutVolume) {
            name = "kaptonReadout"
            position {
                z = -chamberHeight / 2 - readoutKaptonThickness / 2
                unit = LUnit.MM
            }
        }
        physVolume(copperReadoutVolume) {
            name = "copperReadout"
            position {
                z = -chamberHeight / 2 + readoutCopperThickness / 2
                unit = LUnit.MM
            }
            rotation { z = 45 }
        }
        physVolume(chamberBodyVolume) {
            name = "chamberBody"
        }
        physVolume(chamberBackplateVolume) {
            name = "chamberBackplate"
            position {
                z = -chamberHeight / 2 - readoutKaptonThickness - chamberBackplateThickness / 2
                unit = LUnit.MM
            }
        }
        physVolume(chamberTeflonWallVolume) {
            name = "chamberTeflonWall"
        }
        physVolume(cathodeTeflonDiskVolume) {
            name = "cathodeTeflonDisk"
            position {
                z = chamberHeight / 2 + cathodeTeflonDiskThickness / 2
                unit = LUnit.MM
            }
        }
        physVolume(cathodeCopperDiskVolume) {
            name = "cathodeCopperDisk"
            position {
                z = chamberHeight / 2 + cathodeCopperSupportThickness / 2
                unit = LUnit.MM
            }
        }
        physVolume(cathodeWindowVolume) {
            name = "cathodeWindow"
            position {
                z = chamberHeight / 2 - mylarCathodeThickness / 2
                unit = LUnit.MM
            }
        }
        physVolume(cathodeFillingVolume) {
            name = "cathodeFilling"
            position {
                z = chamberHeight / 2 + cathodeTeflonDiskThickness / 2
                unit = LUnit.MM
            }
        }
        physVolume(leadShieldingVolume) {
            name = "leadShielding"
            position {
                z = -shieldingOffset
                unit = LUnit.MM
            }
        }
        physVolume(detectorPipeVolume) {
            name = "detectorPipe"
            position {
                z = detectorPipeZinWorld
                unit = LUnit.MM
            }
        }
        physVolume(detectorPipeFillingVolume) {
            name = "detectorPipeFilling"
            position {
                z = detectorPipeZinWorld + detectorPipeFillingOffsetWithPipe
                unit = LUnit.MM
            }
        }
    }
}
 