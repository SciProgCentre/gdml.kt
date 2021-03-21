package space.kscience.gdml

/**
 * A complete gdml examples for demonstrations and tests
 */
public object GdmlShowCase {
    public val cubes: Gdml = Gdml {
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
    }

    public val babyIaxo: Gdml = Gdml {
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
            val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

            val shieldingMaterial = materials.composite("G4_Pb")
            val scintillatorMaterial = materials.composite("BC408")
            val captureMaterial = materials.composite("G4_Cd")

            // chamber
            val copperMaterial = materials.composite("G4_Cu")
            val chamberSolidBase =
                solids.box(chamberOuterSquareSide, chamberOuterSquareSide, chamberHeight, "chamberSolidBase")
            val chamberSolidHole = solids.tube(chamberDiameter / 2, chamberHeight, "chamberSolidHole")
            val chamberSolid = solids.subtraction(chamberSolidBase, chamberSolidHole, "chamberSolid")
            val chamberBodyVolume = volume(copperMaterial, chamberSolid, "chamberBodyVolume")
            val chamberBackplateSolid = solids.box(chamberOuterSquareSide,
                chamberOuterSquareSide,
                chamberBackplateThickness,
                "chamberBackplateSolid"
            )
            val chamberBackplateVolume = volume(copperMaterial, chamberBackplateSolid, "chamberBackplateVolume")
            // chamber teflon walls
            val teflonMaterial = materials.composite("G4_TEFLON")
            val chamberTeflonWallSolid = solids.tube(chamberDiameter / 2, chamberHeight, "chamberTeflonWallSolid") {
                rmin = chamberDiameter / 2 - chamberTeflonWallThickness
            }
            val chamberTeflonWallVolume = volume(teflonMaterial, chamberTeflonWallSolid, "chamberTeflonWallVolume")
            // cathode
            val cathodeCopperDiskMaterial = materials.composite("G4_Cu")
            val cathodeWindowMaterial = materials.composite("G4_MYLAR")

            val cathodeTeflonDiskSolidBase =
                solids.tube(chamberOuterSquareSide / 2, cathodeTeflonDiskThickness, "cathodeTeflonDiskSolidBase") {
                    rmin = cathodeTeflonDiskHoleRadius
                }
            val cathodeCopperDiskSolid =
                solids.tube(cathodeCopperSupportOuterRadius, cathodeCopperSupportThickness, "cathodeCopperDiskSolid") {
                    rmin = cathodeCopperSupportInnerRadius
                }

            val cathodeTeflonDiskSolid =
                solids.subtraction(cathodeTeflonDiskSolidBase, cathodeCopperDiskSolid, "cathodeTeflonDiskSolid")
            val cathodeTeflonDiskVolume = volume(teflonMaterial, cathodeTeflonDiskSolid, "cathodeTeflonDiskVolume")

            val cathodeWindowSolid =
                solids.tube(cathodeTeflonDiskHoleRadius, mylarCathodeThickness, "cathodeWindowSolid")
            val cathodeWindowVolume = volume(cathodeWindowMaterial, cathodeWindowSolid, "cathodeWindowVolume")

            val vacuumMaterial = materials.composite("G4_Galactic")
            val cathodeFillingSolidBase =
                solids.tube(cathodeTeflonDiskHoleRadius, cathodeTeflonDiskThickness, "cathodeFillingSolidBase")

            val cathodeFillingSolid =
                solids.subtraction(cathodeFillingSolidBase, cathodeCopperDiskSolid, "cathodeFillingSolid") {
                    position(z = chamberHeight / 2 - mylarCathodeThickness / 2)
                }
            val cathodeFillingVolume = volume(vacuumMaterial, cathodeFillingSolid, "cathodeFillingVolume")

            // pattern
            val cathodePatternLineAux = solids.box(
                cathodePatternLineWidth,
                cathodeCopperSupportInnerRadius * 2,
                cathodeCopperSupportThickness,
                "cathodePatternLineAux"
            )
            val cathodePatternCentralHole = solids.tube(
                cathodePatternDiskRadius - 0 * cathodePatternLineWidth,
                cathodeCopperSupportThickness * 1.1,
                "cathodePatternCentralHole"
            )
            val cathodePatternLine =
                solids.subtraction(cathodePatternLineAux, cathodePatternCentralHole, "cathodePatternLine")

            val cathodePatternDisk = solids.tube(
                cathodePatternDiskRadius,
                cathodeCopperSupportThickness,
                "cathodePatternDisk"
            ) { rmin = cathodePatternDiskRadius - cathodePatternLineWidth }


            val cathodeCopperDiskSolidAux0 =
                solids.union(cathodeCopperDiskSolid, cathodePatternLine, "cathodeCopperDiskSolidAux0") {
                    rotation(x = 0, y = 0, z = 0)
                }
            val cathodeCopperDiskSolidAux1 =
                solids.union(cathodeCopperDiskSolidAux0, cathodePatternLine, "cathodeCopperDiskSolidAux1") {
                    rotation(x = 0, y = 0, z = 45)
                }
            val cathodeCopperDiskSolidAux2 =
                solids.union(cathodeCopperDiskSolidAux1, cathodePatternLine, "cathodeCopperDiskSolidAux2") {
                    rotation(x = 0, y = 0, z = 90)
                }
            val cathodeCopperDiskSolidAux3 =
                solids.union(cathodeCopperDiskSolidAux2, cathodePatternLine, "cathodeCopperDiskSolidAux3") {
                    rotation(x = 0, y = 0, z = 135)
                }

            val cathodeCopperDiskFinal =
                solids.union(cathodeCopperDiskSolidAux3, cathodePatternDisk, "cathodeCopperDiskFinal")


            val cathodeCopperDiskVolume =
                volume(cathodeCopperDiskMaterial, cathodeCopperDiskFinal, "cathodeCopperDiskFinal")

            val gasSolidOriginal = solids.tube(
                chamberDiameter / 2 - chamberTeflonWallThickness,
                chamberHeight,
                "gasSolidOriginal"
            )

            val kaptonReadoutMaterial = materials.composite("G4_KAPTON")
            val kaptonReadoutSolid =
                solids.box(chamberOuterSquareSide, chamberOuterSquareSide, readoutKaptonThickness, "kaptonReadoutSolid")
            val kaptonReadoutVolume = volume(kaptonReadoutMaterial, kaptonReadoutSolid, "kaptonReadoutVolume")

            val copperReadoutSolid =
                solids.box(readoutPlaneSide, readoutPlaneSide, readoutCopperThickness, "copperReadoutSolid")
            val copperReadoutVolume = volume(copperMaterial, copperReadoutSolid, "copperReadoutVolume")

            val gasSolidAux =
                solids.subtraction(gasSolidOriginal, copperReadoutSolid, "gasSolidAux") {
                    position(z = -chamberHeight / 2 + readoutCopperThickness / 2)
                }

            val gasMaterial = materials.composite("G4_Ar")
            val gasSolid =
                solids.subtraction(gasSolidAux, cathodeWindowSolid, "gasSolid") {
                    position(z = chamberHeight / 2 - mylarCathodeThickness / 2)
                    rotation(z = 45)
                }
            val gasVolume = volume(gasMaterial, gasSolid, "gasVolume")

            val leadBoxSolid = solids.box(leadBoxSizeXY, leadBoxSizeXY, leadBoxSizeZ, "leadBoxSolid")
            val leadBoxShaftSolid = solids.box(
                leadBoxShaftShortSideX,
                leadBoxShaftShortSideY,
                leadBoxShaftLongSide,
                "leadBoxShaftSolid"
            )
            val leadBoxWithShaftSolid = solids.subtraction(leadBoxSolid, leadBoxShaftSolid, "leadBoxWithShaftSolid") {

                position(z = leadBoxSizeZ / 2 - leadBoxShaftLongSide / 2)
            }
            val leadShieldingVolume = volume(shieldingMaterial, leadBoxWithShaftSolid, "ShieldingVolume")

            val detectorPipeChamberFlangeSolid = solids.tube(detectorPipeChamberFlangeRadius,
                detectorPipeChamberFlangeThickness,
                "detectorPipeChamberFlangeSolid")
            val detectorPipeTelescopeFlangeSolid = solids.tube(detectorPipeTelescopeFlangeRadius,
                detectorPipeTelescopeFlangeThickness,
                "detectorPipeTelescopeFlangeSolid")
            val detectorPipeSection1of2Solid =
                solids.tube(detectorPipeOuterRadius1, detectorPipeSection1of2Length, "detectorPipeSection1of2Solid")
            val detectorPipeSection2of2Solid =
                solids.tube(detectorPipeOuterRadius2, detectorPipeSection2of2Length, "detectorPipeSection2of2Solid")
            val detectorPipeAux1 =
                solids.union(detectorPipeChamberFlangeSolid, detectorPipeSection1of2Solid, "detectorPipeAux1") {
                    position(z = detectorPipeUnion1Z)
                }
            val detectorPipeAux2 = solids.union(detectorPipeAux1, detectorPipeSection2of2Solid, "detectorPipeAux2") {
                position(z = detectorPipeUnion2Z)
            }
            val detectorPipeNotEmpty =
                solids.union(detectorPipeAux2, detectorPipeTelescopeFlangeSolid, "detectorPipeNotEmpty") {
                    position(z = detectorPipeUnion3Z)
                }

            val detectorPipeInside1of3Solid = solids.tube(detectorPipeInsideSection1of3Radius,
                detectorPipeInsideSection1of3Length,
                "detectorPipeInside1of3Solid")
            val detectorPipeInside2of3Solid = solids.tube(detectorPipeInsideSection2of3Radius,
                detectorPipeInsideSection2of3Length,
                "detectorPipeInside2of3Solid")
            val detectorPipeInside3of3Solid = solids.tube(detectorPipeInsideSection3of3Radius,
                detectorPipeInsideSection3of3Length,
                "detectorPipeInside3of3Solid")

            val detectorPipeInsideCone1of3Solid =
                solids.cone(detectorPipeInsideCone1of3Length, detectorPipeInsideSection1of3Radius,
                    detectorPipeInsideSection2of3Radius, "detectorPipeInsideCone1of3Solid")
            val detectorPipeInsideCone2of3Solid =
                solids.cone(detectorPipeInsideCone2of3Length, detectorPipeInsideSection2of3Radius,
                    detectorPipeInsideSection3of3Radius, "detectorPipeInsideCone2of3Solid")
            val detectorPipeInsideCone3of3Solid =
                solids.cone(detectorPipeInsideCone3of3Length, detectorPipeInsideSection3of3Radius,
                    detectorPipeInsideSectionTelescopeRadius, "detectorPipeInsideCone3of3Solid")

            val detectorPipeInsideAux1 =
                solids.union(detectorPipeInside1of3Solid, detectorPipeInsideCone1of3Solid, "detectorPipeInsideAux1") {
                    position(z = detectorPipeInsideUnion1Z)
                }
            val detectorPipeInsideAux2 =
                solids.union(detectorPipeInsideAux1, detectorPipeInside2of3Solid, "detectorPipeInsideAux2") {
                    position(z = detectorPipeInsideUnion2Z)
                }
            val detectorPipeInsideAux3 =
                solids.union(detectorPipeInsideAux2, detectorPipeInsideCone2of3Solid, "detectorPipeInsideAux3") {
                    position(z = detectorPipeInsideUnion3Z)
                }
            val detectorPipeInsideAux4 =
                solids.union(detectorPipeInsideAux3, detectorPipeInside3of3Solid, "detectorPipeInsideAux4") {
                    position(z = detectorPipeInsideUnion4Z)
                }
            val detectorPipeInside =
                solids.union(detectorPipeInsideAux4, detectorPipeInsideCone3of3Solid, "detectorPipeInside") {
                    position(z = detectorPipeInsideUnion5Z)
                }
            val detectorPipeSolid = solids.subtraction(detectorPipeNotEmpty, detectorPipeInside, "detectorPipeSolid") {
                position(z = detectorPipeInsideSection1of3Length / 2 - detectorPipeChamberFlangeThickness / 2)
            }
            val detectorPipeVolume = volume(copperMaterial, detectorPipeSolid, "detectorPipeVolume")
            val detectorPipeFillingVolume = volume(vacuumMaterial, detectorPipeInside, "detectorPipeFillingVolume")

            // world setup
            world = volume(worldMaterial, worldBox, "world") {
                physVolume(gasVolume) {
                    name = "gas"
                }
                physVolume(kaptonReadoutVolume) {
                    name = "kaptonReadout"
                    position(z = -chamberHeight / 2 - readoutKaptonThickness / 2)

                }
                physVolume(copperReadoutVolume) {
                    name = "copperReadout"
                    position(z = -chamberHeight / 2 + readoutCopperThickness / 2)
                    rotation(z = 45)
                }
                physVolume(chamberBodyVolume) {
                    name = "chamberBody"
                }
                physVolume(chamberBackplateVolume) {
                    name = "chamberBackplate"
                    position(z = -chamberHeight / 2 - readoutKaptonThickness - chamberBackplateThickness / 2)
                }
                physVolume(chamberTeflonWallVolume) {
                    name = "chamberTeflonWall"
                }
                physVolume(cathodeTeflonDiskVolume) {
                    name = "cathodeTeflonDisk"
                    position(z = chamberHeight / 2 + cathodeTeflonDiskThickness / 2)
                }
                physVolume(cathodeCopperDiskVolume) {
                    name = "cathodeCopperDisk"
                    position(z = chamberHeight / 2 + cathodeCopperSupportThickness / 2)
                }
                physVolume(cathodeWindowVolume) {
                    name = "cathodeWindow"
                    position(z = chamberHeight / 2 - mylarCathodeThickness / 2)
                }
                physVolume(cathodeFillingVolume) {
                    name = "cathodeFilling"
                    position(z = chamberHeight / 2 + cathodeTeflonDiskThickness / 2)
                }
                physVolume(leadShieldingVolume) {
                    name = "leadShielding"
                    position(z = -shieldingOffset)
                }
                physVolume(detectorPipeVolume) {
                    name = "detectorPipe"
                    position(z = detectorPipeZinWorld)
                }
                physVolume(detectorPipeFillingVolume) {
                    name = "detectorPipeFilling"
                    position(z = detectorPipeZinWorld + detectorPipeFillingOffsetWithPipe)
                }
            }
        }
    }.withUnits(LUnit.MM, AUnit.RAD)
}