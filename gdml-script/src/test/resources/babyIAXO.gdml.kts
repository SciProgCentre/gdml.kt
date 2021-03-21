import space.kscience.gdml.*

/*
    Author: Luis Antonio Obis Aparicio (lobis@unizar.es) University of Zaragoza
    Implementation of the tentative geometry for the BabyIAXO experiment (IAXO Collaboration)
 */

//----------------------------------------------------------------------------------------------------------------------

/*
    Enums are used to better organize geometrical variables
 */
enum class Chamber(val mm: Double) {
    // Body + Backplate
    Height(30.0), Diameter(102.0),
    BackplateThickness(15.0), SquareSide(134.0),
    TeflonWallThickness(1.0),

    // Readout
    ReadoutKaptonThickness(0.5), ReadoutCopperThickness(0.2),
    ReadoutPlaneSide(60.0),

    // Cathode
    CathodeTeflonDiskHoleRadius(15.0), CathodeTeflonDiskThickness(5.0),
    CathodeCopperSupportOuterRadius(45.0), CathodeCopperSupportInnerRadius(8.5),
    CathodeCopperSupportThickness(1.0), CathodeWindowThickness(0.004),
    CathodePatternDiskRadius(4.25), CathodePatternLineWidth(0.3),
}

enum class DetectorPipe(val mm: Double) {
    TotalLength(491.0),

    // Outside
    ChamberFlangeThickness(14.0), ChamberFlangeRadius(Chamber.SquareSide.mm / 2),
    TelescopeFlangeThickness(18.0), TelescopeFlangeRadius(150.0 / 2),
    Section2of2Length(150.0 - TelescopeFlangeThickness.mm),
    Section1of2Length(TotalLength.mm - TelescopeFlangeThickness.mm - ChamberFlangeThickness.mm - Section2of2Length.mm),
    OuterRadius1(92.0 / 2), OuterRadius2(108.0 / 2),
    Union1Z(ChamberFlangeThickness.mm / 2 + Section1of2Length.mm / 2),
    Union2Z(Union1Z.mm + Section1of2Length.mm / 2 + Section2of2Length.mm / 2),
    Union3Z(Union2Z.mm + Section2of2Length.mm / 2 + TelescopeFlangeThickness.mm / 2),

    // Inside
    InsideSection1of3Radius(43.0 / 2), InsideSection2of3Radius(68.0 / 2), InsideSection3of3Radius(85.0 / 2),
    InsideSectionTelescopeRadius(108.0 / 2),
    InsideCone1of3Length(21.65), InsideCone2of3Length(14.72), InsideCone3of3Length(9.0),
    InsideSection3of3Length(115.0 - InsideCone3of3Length.mm),
    InsideSection2of3Length(290.0 - InsideSection3of3Length.mm - InsideCone3of3Length.mm - InsideCone2of3Length.mm),
    InsideSection1of3Length(201.0 - InsideCone1of3Length.mm),
    InsideUnion1Z(InsideSection1of3Length.mm / 2 + InsideCone1of3Length.mm / 2),
    InsideUnion2Z(InsideUnion1Z.mm + InsideCone1of3Length.mm / 2 + InsideSection2of3Length.mm / 2),
    InsideUnion3Z(InsideUnion2Z.mm + InsideSection2of3Length.mm / 2 + InsideCone2of3Length.mm / 2),
    InsideUnion4Z(InsideUnion3Z.mm + InsideCone2of3Length.mm / 2 + InsideSection3of3Length.mm / 2),
    InsideUnion5Z(InsideUnion4Z.mm + InsideSection3of3Length.mm / 2 + InsideCone3of3Length.mm / 2),
    FillingOffsetWithPipe(InsideSection1of3Length.mm / 2 - ChamberFlangeThickness.mm / 2),

    // World
    ZinWorld(ChamberFlangeThickness.mm / 2 + Chamber.Height.mm / 2 + Chamber.CathodeTeflonDiskThickness.mm),
}

enum class Shielding(val mm: Double) {
    SizeXY(590.0), SizeZ(540.0),
    ShaftShortSideX(194.0), ShaftShortSideY(170.0),
    ShaftLongSide(340.0),
    DetectorToShieldingSeparation(-60.0), EnvelopeThickness(10.0),
    OffsetZ(DetectorToShieldingSeparation.mm + Chamber.Height.mm / 2 + Chamber.ReadoutKaptonThickness.mm + Chamber.BackplateThickness.mm),
}

// vetoes
enum class VetoLengths(val mm: Double, val namePrefix: String) {
    LongSideStandard(800.0, "standard"), LongSideSmall(300.0, "small"), LongSideLarge(1500.0, "large"),
}

enum class Veto(val mm: Double) {
    Width(200.0), Thickness(50.0), LongSide(VetoLengths.LongSideStandard.mm),
    CaptureLayerThickness(1.0), WrappingThickness(1.0),
    SeparationAdjacent(5.0),
    FullThickness(Thickness.mm + 2 * WrappingThickness.mm + 2 * CaptureLayerThickness.mm),

    // light guide and PMT
    LightGuide1Length(130.0), LightGuide2Length(80.0),
    PhotomultiplierLength(233.5), PhotomultiplierDiameter(70.0),
}

/*
    We pull materials from remote GitHub repository
    Currently I am organizing them into a map, but I don't think this is the best way.
    Sadly I can't use an enum here, since I need to be in the 'Gdml' context and I can't declare a class there.
    TODO: find a better way to organize materials which supports autocompletion
 */
loadMaterialsFromUrl("https://raw.githubusercontent.com/rest-for-physics/materials/4e2e72017e83ab6c2947e77f04365fbb92c42dc7/materials.xml")

/*
We pull materials from remote GitHub repository
Currently I am organizing them into a map, but I don't think this is the best way.
Sadly I can't use an enum here, since I need to be in the 'Gdml' context and I can't declare a class there.
TODO: find a better way to organize materials which supports autocompletion
*/
//loadMaterialsFromUrl("https://raw.githubusercontent.com/rest-for-physics/materials/4e2e72017e83ab6c2947e77f04365fbb92c42dc7/materials.xml")


val iaxoMaterials = object {
    val world = materials.composite("G4_AIR")
    val gas = materials.composite("G4_Ar")
    val vacuum = materials.composite("G4_Galactic")
    val copper = materials.composite("G4_Cu")
    val lead = materials.composite("G4_Pb")
    val teflon = materials.composite("G4_TEFLON")
    val kapton = materials.composite("G4_KAPTON")
    val mylar = materials.composite("G4_MYLAR")
    val scintillator = materials.composite("BC408")
    val lightGuide = materials.composite("G4_LUCITE")
    val neutronCapture = materials.composite("G4_Cd")
    val scintillatorWrapping = materials.composite("G4_NEOPRENE")
    val pmt = materials.composite("G4_STAINLESS-STEEL")
}

structure {
    val chamberVolume: GdmlRef<GdmlAssembly> by lazy {
        val chamberBodySolid = solids.subtraction(
            solids.box(
                GdmlShowCase.Chamber.SquareSide.mm,
                GdmlShowCase.Chamber.SquareSide.mm,
                GdmlShowCase.Chamber.Height.mm,
                "chamberBodyBaseSolid"
            ), solids.tube(GdmlShowCase.Chamber.Diameter.mm / 2, GdmlShowCase.Chamber.Height.mm, "chamberBodyHoleSolid"),
            "chamberBodySolid"
        )
        val chamberBodyVolume = volume(iaxoMaterials.copper, chamberBodySolid, "chamberBodyVolume")

        val chamberBackplateSolid = solids.box(
            GdmlShowCase.Chamber.SquareSide.mm,
            GdmlShowCase.Chamber.SquareSide.mm,
            GdmlShowCase.Chamber.BackplateThickness.mm,
            "chamberBackplateSolid"
        )
        val chamberBackplateVolume =
            volume(iaxoMaterials.copper, chamberBackplateSolid, "chamberBackplateVolume")

        val chamberTeflonWallSolid =
            solids.tube(GdmlShowCase.Chamber.Diameter.mm / 2, GdmlShowCase.Chamber.Height.mm, "chamberTeflonWallSolid") {
                rmin = GdmlShowCase.Chamber.Diameter.mm / 2 - GdmlShowCase.Chamber.TeflonWallThickness.mm
            }
        val chamberTeflonWallVolume =
            volume(iaxoMaterials.teflon, chamberTeflonWallSolid, "chamberTeflonWallVolume")
        // readout
        val kaptonReadoutSolid = solids.box(
            GdmlShowCase.Chamber.SquareSide.mm,
            GdmlShowCase.Chamber.SquareSide.mm,
            GdmlShowCase.Chamber.ReadoutKaptonThickness.mm,
            "kaptonReadoutSolid"
        )
        val kaptonReadoutVolume = volume(iaxoMaterials.kapton, kaptonReadoutSolid, "kaptonReadoutVolume")

        val copperReadoutSolid = solids.box(
            GdmlShowCase.Chamber.ReadoutPlaneSide.mm,
            GdmlShowCase.Chamber.ReadoutPlaneSide.mm,
            GdmlShowCase.Chamber.ReadoutCopperThickness.mm,
            "copperReadoutSolid"
        )

        val copperReadoutVolume = volume(iaxoMaterials.kapton, copperReadoutSolid, "copperReadoutVolume")

        // cathode
        val cathodeTeflonDiskBaseSolid = solids.tube(
            GdmlShowCase.Chamber.SquareSide.mm / 2,
            GdmlShowCase.Chamber.CathodeTeflonDiskThickness.mm,
            "cathodeTeflonDiskBaseSolid"
        ) {
            rmin = GdmlShowCase.Chamber.CathodeTeflonDiskHoleRadius.mm
        }

        val cathodeCopperDiskSolid = solids.tube(
            GdmlShowCase.Chamber.CathodeCopperSupportOuterRadius.mm,
            GdmlShowCase.Chamber.CathodeCopperSupportThickness.mm,
            "cathodeCopperDiskSolid"
        ) {
            rmin = GdmlShowCase.Chamber.CathodeCopperSupportInnerRadius.mm
        }

        val cathodeTeflonDiskSolid =
            solids.subtraction(cathodeTeflonDiskBaseSolid,
                cathodeCopperDiskSolid,
                "cathodeTeflonDiskSolid") {
                position(z = -GdmlShowCase.Chamber.CathodeTeflonDiskThickness.mm / 2 + GdmlShowCase.Chamber.CathodeCopperSupportThickness.mm / 2)
            }

        val cathodeTeflonDiskVolume =
            volume(iaxoMaterials.teflon, cathodeTeflonDiskSolid, "cathodeTeflonDiskVolume") {}

        val cathodeWindowSolid =
            solids.tube(
                GdmlShowCase.Chamber.CathodeTeflonDiskHoleRadius.mm,
                GdmlShowCase.Chamber.CathodeWindowThickness.mm,
                "cathodeWindowSolid"
            )
        val cathodeWindowVolume = volume(iaxoMaterials.mylar, cathodeWindowSolid, "cathodeWindowVolume")

        // cathode copper disk pattern
        val cathodePatternLineAux = solids.box(
            GdmlShowCase.Chamber.CathodePatternLineWidth.mm,
            GdmlShowCase.Chamber.CathodeCopperSupportInnerRadius.mm * 2,
            GdmlShowCase.Chamber.CathodeCopperSupportThickness.mm,
            "cathodePatternLineAux"
        )
        val cathodePatternCentralHole = solids.tube(
            GdmlShowCase.Chamber.CathodePatternDiskRadius.mm,
            GdmlShowCase.Chamber.CathodeCopperSupportThickness.mm * 1.1, "cathodePatternCentralHole"
        )
        val cathodePatternLine =
            solids.subtraction(cathodePatternLineAux, cathodePatternCentralHole, "cathodePatternLine")

        val cathodePatternDisk = solids.tube(
            GdmlShowCase.Chamber.CathodePatternDiskRadius.mm,
            GdmlShowCase.Chamber.CathodeCopperSupportThickness.mm, "cathodePatternDisk"
        ) { rmin = GdmlShowCase.Chamber.CathodePatternDiskRadius.mm - GdmlShowCase.Chamber.CathodePatternLineWidth.mm }


        var cathodeCopperDiskSolidAux: GdmlRef<GdmlUnion> = GdmlRef("")

        for (i in 0..3) {
            cathodeCopperDiskSolidAux =
                solids.union(
                    if (i > 0) cathodeCopperDiskSolidAux else cathodeCopperDiskSolid,
                    cathodePatternLine, "cathodeCopperDiskSolidAux$i"
                ) {
                    rotation(x = 0, y = 0, z = 45 * i) {
                        unit = AUnit.DEG
                    }
                }
        }

        val cathodeCopperDiskFinal =
            solids.union(cathodeCopperDiskSolidAux, cathodePatternDisk, "cathodeCopperDiskFinal.solid")

        val cathodeCopperDiskVolume =
            volume(iaxoMaterials.copper, cathodeCopperDiskFinal, "cathodeCopperDiskFinal")

        val cathodeFillingBaseSolid = solids.tube(
            GdmlShowCase.Chamber.CathodeTeflonDiskHoleRadius.mm,
            GdmlShowCase.Chamber.CathodeTeflonDiskThickness.mm,
            "cathodeFillingBaseSolid"
        )
        val cathodeFillingSolid =
            solids.subtraction(cathodeFillingBaseSolid, cathodeCopperDiskFinal, "cathodeFillingSolid") {
                position(z = -GdmlShowCase.Chamber.CathodeTeflonDiskThickness.mm / 2 + GdmlShowCase.Chamber.CathodeCopperSupportThickness.mm / 2)
            }
        val cathodeFillingVolume =
            volume(iaxoMaterials.vacuum, cathodeFillingSolid, "cathodeFillingVolume") {}

        // gas
        val gasSolidOriginal = solids.tube(
            GdmlShowCase.Chamber.Diameter.mm / 2 - GdmlShowCase.Chamber.TeflonWallThickness.mm,
            GdmlShowCase.Chamber.Height.mm, "gasSolidOriginal"
        )
        val gasSolidAux = solids.subtraction(gasSolidOriginal, copperReadoutSolid, "gasSolidAux") {
            position(z = -GdmlShowCase.Chamber.Height.mm / 2 + GdmlShowCase.Chamber.ReadoutCopperThickness.mm / 2)
            rotation(z = 45) { unit = AUnit.DEG }
        }
        val gasSolid =
            solids.subtraction(gasSolidAux, cathodeWindowSolid, "gasSolid") {
                position(z = GdmlShowCase.Chamber.Height.mm / 2 - GdmlShowCase.Chamber.CathodeWindowThickness.mm / 2)
            }
        val gasVolume = volume(iaxoMaterials.gas, gasSolid, "gasVolume")

        return@lazy assembly {
            physVolume(gasVolume, name = "gas")
            physVolume(chamberBackplateVolume, name = "chamberBackplate") {
                position(
                    z = -GdmlShowCase.Chamber.Height.mm / 2 - GdmlShowCase.Chamber.ReadoutKaptonThickness.mm - GdmlShowCase.Chamber.BackplateThickness.mm / 2
                )
            }
            physVolume(chamberBodyVolume, name = "chamberBody")
            physVolume(chamberTeflonWallVolume, name = "chamberTeflonWall")
            physVolume(kaptonReadoutVolume, name = "kaptonReadout") {
                position(z = -GdmlShowCase.Chamber.Height.mm / 2 - GdmlShowCase.Chamber.ReadoutKaptonThickness.mm / 2)
            }
            physVolume(copperReadoutVolume, name = "copperReadout") {
                position(z = -GdmlShowCase.Chamber.Height.mm / 2 + GdmlShowCase.Chamber.ReadoutCopperThickness.mm / 2)
                rotation(z = 45) { unit = AUnit.DEG }
            }
            physVolume(cathodeWindowVolume, name = "cathodeWindow") {
                position(z = GdmlShowCase.Chamber.Height.mm / 2 - GdmlShowCase.Chamber.CathodeWindowThickness.mm / 2)
            }
            physVolume(cathodeTeflonDiskVolume, name = "cathodeTeflonDisk") {
                position(z = GdmlShowCase.Chamber.Height.mm / 2 + GdmlShowCase.Chamber.CathodeTeflonDiskThickness.mm / 2)
            }
            physVolume(cathodeFillingVolume, name = "cathodeFilling") {
                position(z = GdmlShowCase.Chamber.Height.mm / 2 + GdmlShowCase.Chamber.CathodeTeflonDiskThickness.mm / 2)
            }
            physVolume(cathodeCopperDiskVolume, name = "cathodeCopperDiskPattern") {
                position(z = GdmlShowCase.Chamber.Height.mm / 2 + GdmlShowCase.Chamber.CathodeCopperSupportThickness.mm / 2)
            }
        }
    }

    //lazyly initialize the value to avoid several calls
    val detectorPipeVolume: GdmlRef<GdmlAssembly> by lazy {

        val detectorPipeChamberFlangeSolid = solids.tube(
            GdmlShowCase.DetectorPipe.ChamberFlangeRadius.mm,
            GdmlShowCase.DetectorPipe.ChamberFlangeThickness.mm,
            "detectorPipeChamberFlangeSolid"
        )
        val detectorPipeTelescopeFlangeSolid = solids.tube(
            GdmlShowCase.DetectorPipe.TelescopeFlangeRadius.mm,
            GdmlShowCase.DetectorPipe.TelescopeFlangeThickness.mm,
            "detectorPipeTelescopeFlangeSolid"
        )
        val detectorPipeSection1of2Solid =
            solids.tube(
                GdmlShowCase.DetectorPipe.OuterRadius1.mm,
                GdmlShowCase.DetectorPipe.Section1of2Length.mm,
                "detectorPipeSection1of2Solid"
            )
        val detectorPipeSection2of2Solid =
            solids.tube(
                GdmlShowCase.DetectorPipe.OuterRadius2.mm,
                GdmlShowCase.DetectorPipe.Section2of2Length.mm,
                "detectorPipeSection2of2Solid"
            )
        val detectorPipeAux1 =
            solids.union(detectorPipeChamberFlangeSolid, detectorPipeSection1of2Solid, "detectorPipeAux1")
            {
                position(z = GdmlShowCase.DetectorPipe.Union1Z.mm)
            }
        val detectorPipeAux2 =
            solids.union(detectorPipeAux1, detectorPipeSection2of2Solid, "detectorPipeAux2")
            {
                position(z = GdmlShowCase.DetectorPipe.Union2Z.mm)
            }
        val detectorPipeNotEmpty =
            solids.union(detectorPipeAux2, detectorPipeTelescopeFlangeSolid, "detectorPipeNotEmpty")
            {
                position(z = GdmlShowCase.DetectorPipe.Union3Z.mm)
            }
        val detectorPipeInside1of3Solid = solids.tube(
            GdmlShowCase.DetectorPipe.InsideSection1of3Radius.mm,
            GdmlShowCase.DetectorPipe.InsideSection1of3Length.mm,
            "detectorPipeInside1of3Solid"
        )
        val detectorPipeInside2of3Solid = solids.tube(
            GdmlShowCase.DetectorPipe.InsideSection2of3Radius.mm,
            GdmlShowCase.DetectorPipe.InsideSection2of3Length.mm,
            "detectorPipeInside2of3Solid"
        )
        val detectorPipeInside3of3Solid = solids.tube(
            GdmlShowCase.DetectorPipe.InsideSection3of3Radius.mm,
            GdmlShowCase.DetectorPipe.InsideSection3of3Length.mm,
            "detectorPipeInside3of3Solid"
        )
        val detectorPipeInsideCone1of3Solid = solids.cone(
            GdmlShowCase.DetectorPipe.InsideCone1of3Length.mm,
            GdmlShowCase.DetectorPipe.InsideSection1of3Radius.mm, GdmlShowCase.DetectorPipe.InsideSection2of3Radius.mm,
            "detectorPipeInsideCone1of3Solid"
        )
        val detectorPipeInsideCone2of3Solid = solids.cone(
            GdmlShowCase.DetectorPipe.InsideCone2of3Length.mm,
            GdmlShowCase.DetectorPipe.InsideSection2of3Radius.mm, GdmlShowCase.DetectorPipe.InsideSection3of3Radius.mm,
            "detectorPipeInsideCone2of3Solid"
        )
        val detectorPipeInsideCone3of3Solid = solids.cone(
            GdmlShowCase.DetectorPipe.InsideCone3of3Length.mm,
            GdmlShowCase.DetectorPipe.InsideSection3of3Radius.mm, GdmlShowCase.DetectorPipe.InsideSectionTelescopeRadius.mm,
            "detectorPipeInsideCone3of3Solid"
        )
        val detectorPipeInsideAux1 =
            solids.union(detectorPipeInside1of3Solid,
                detectorPipeInsideCone1of3Solid,
                "detectorPipeInsideAux1") {
                position(z = GdmlShowCase.DetectorPipe.InsideUnion1Z.mm)
            }
        val detectorPipeInsideAux2 =
            solids.union(detectorPipeInsideAux1, detectorPipeInside2of3Solid, "detectorPipeInsideAux2") {
                position(z = GdmlShowCase.DetectorPipe.InsideUnion2Z.mm)
            }
        val detectorPipeInsideAux3 =
            solids.union(detectorPipeInsideAux2,
                detectorPipeInsideCone2of3Solid,
                "detectorPipeInsideAux3") {
                position(z = GdmlShowCase.DetectorPipe.InsideUnion3Z.mm)
            }
        val detectorPipeInsideAux4 =
            solids.union(detectorPipeInsideAux3, detectorPipeInside3of3Solid, "detectorPipeInsideAux4") {
                position(z = GdmlShowCase.DetectorPipe.InsideUnion4Z.mm)
            }
        val detectorPipeInside =
            solids.union(detectorPipeInsideAux4, detectorPipeInsideCone3of3Solid, "detectorPipeInside") {
                position(z = GdmlShowCase.DetectorPipe.InsideUnion5Z.mm)
            }
        val detectorPipeSolid =
            solids.subtraction(detectorPipeNotEmpty, detectorPipeInside, "detectorPipeSolid") {
                position(z = GdmlShowCase.DetectorPipe.InsideSection1of3Length.mm / 2 - GdmlShowCase.DetectorPipe.ChamberFlangeThickness.mm / 2)
            }
        val detectorPipeVolume = volume(iaxoMaterials.copper!!, detectorPipeSolid, "detectorPipeVolume")
        val detectorPipeFillingVolume =
            volume(iaxoMaterials.vacuum, detectorPipeInside, "detectorPipeFillingVolume")

        return@lazy assembly {
            physVolume(detectorPipeVolume) {
                name = "detectorPipe"
            }
            physVolume(detectorPipeFillingVolume) {
                name = "detectorPipeFilling"
                position { z = GdmlShowCase.DetectorPipe.FillingOffsetWithPipe.mm }
            }
        }
    }


    val shieldingVolume: GdmlRef<GdmlAssembly> by lazy {
        val leadBoxSolid =
            solids.box(GdmlShowCase.Shielding.SizeXY.mm, GdmlShowCase.Shielding.SizeXY.mm, GdmlShowCase.Shielding.SizeZ.mm, "leadBoxSolid")
        val leadBoxShaftSolid =
            solids.box(
                GdmlShowCase.Shielding.ShaftShortSideX.mm,
                GdmlShowCase.Shielding.ShaftShortSideY.mm,
                GdmlShowCase.Shielding.ShaftLongSide.mm,
                "leadBoxShaftSolid"
            )
        val leadBoxWithShaftSolid =
            solids.subtraction(leadBoxSolid, leadBoxShaftSolid, "leadBoxWithShaftSolid") {
                position(z = GdmlShowCase.Shielding.SizeZ.mm / 2 - GdmlShowCase.Shielding.ShaftLongSide.mm / 2)
            }
        val leadShieldingVolume = volume(iaxoMaterials.lead, leadBoxWithShaftSolid, "ShieldingVolume")
        // lead shielding envelope
        val leadBoxEnvelopeSolid = solids.subtraction(
            solids.subtraction(
                solids.box(
                    GdmlShowCase.Shielding.SizeXY.mm + 2 * GdmlShowCase.Shielding.EnvelopeThickness.mm,
                    GdmlShowCase.Shielding.SizeXY.mm + 2 * GdmlShowCase.Shielding.EnvelopeThickness.mm,
                    GdmlShowCase.Shielding.SizeZ.mm + 2 * GdmlShowCase.Shielding.EnvelopeThickness.mm,
                    "leadBoxEnvelopeBase"
                ), leadBoxSolid,
                "leadBoxEnvelopeWithoutHole"
            ), solids.box(
                GdmlShowCase.Shielding.ShaftShortSideX.mm,
                GdmlShowCase.Shielding.ShaftShortSideY.mm,
                GdmlShowCase.Shielding.EnvelopeThickness.mm,
                "leadBoxEnvelopeHole"
            ), "leadBoxEnvelope"
        ) {
            position(z = GdmlShowCase.Shielding.SizeZ.mm / 2 + GdmlShowCase.Shielding.EnvelopeThickness.mm / 2)
        }

        val leadBoxEnvelopeVolume = volume(
            iaxoMaterials.world,
            leadBoxEnvelopeSolid,
            "leadBoxEnvelopeVolume"
        )

        return@lazy assembly {
            physVolume(leadShieldingVolume, name = "shielding20cm") {
                position(z = -GdmlShowCase.Shielding.OffsetZ.mm)
            }
            physVolume(leadBoxEnvelopeVolume, name = "shieldingEnvelope") {
                position(z = -GdmlShowCase.Shielding.OffsetZ.mm)
            }
        }
    }

    fun veto(length: GdmlShowCase.VetoLengths, includePMT: Boolean = false): GdmlRef<GdmlAssembly> {
        val label = "-${length.mm}mm"
        val scintillatorSolid = solids.box(
            GdmlShowCase.Veto.Width.mm,
            GdmlShowCase.Veto.Thickness.mm,
            length.mm,
            "scintillatorSolid$label",
        )
        val scintillatorVolume =
            volume(iaxoMaterials.scintillator, scintillatorSolid, "scintillatorVolume$label")

        val captureLayerSolid = solids.box(
            GdmlShowCase.Veto.Width.mm,
            GdmlShowCase.Veto.CaptureLayerThickness.mm,
            length.mm,
            "captureLayerSolid$label"
        )
        val captureLayerVolume = volume(
            iaxoMaterials.neutronCapture,
            captureLayerSolid,
            "captureLayerVolume$label"
        )

        val scintillatorWrappingBaseSolid = solids.box(
            GdmlShowCase.Veto.Width.mm + 2 * GdmlShowCase.Veto.WrappingThickness.mm,
            GdmlShowCase.Veto.Thickness.mm + 2 * GdmlShowCase.Veto.WrappingThickness.mm,
            length.mm + 2 * GdmlShowCase.Veto.WrappingThickness.mm,
            "scintillatorWrappingBaseSolid$label"
        )
        val scintillatorWrappingHoleSolid = solids.box(
            GdmlShowCase.Veto.Width.mm,
            GdmlShowCase.Veto.Thickness.mm,
            length.mm,
            "scintillatorWrappingHoleSolid$label",
        )
        val scintillatorWrappingFullSolid = solids.subtraction(
            scintillatorWrappingBaseSolid,
            scintillatorWrappingHoleSolid,
            "scintillatorWrappingFullSolid$label"
        )
        val scintillatorWrappingSolid = solids.subtraction(
            scintillatorWrappingFullSolid,
            solids.box(
                GdmlShowCase.Veto.Width.mm + 2 * GdmlShowCase.Veto.WrappingThickness.mm,
                GdmlShowCase.Veto.Thickness.mm + 2 * GdmlShowCase.Veto.WrappingThickness.mm,
                GdmlShowCase.Veto.WrappingThickness.mm * 1.2, // we remove a bit more, it shouldn't matter since its a sub
                "scintillatorWrappingRemoveSideSolid"
            ), "scintillatorWrappingSolid$label.solid"
        ) {
            position(z = length.mm / 2 + GdmlShowCase.Veto.WrappingThickness.mm / 2)
        }

        val scintillatorWrappingVolume = volume(
            iaxoMaterials.scintillatorWrapping,
            scintillatorWrappingSolid,
            name = "scintillatorWrappingSolid$label"
        )

        // light guide and PMT
        val scintillatorLightGuide1Solid = solids.trd(
            GdmlShowCase.Veto.Width.mm,
            GdmlShowCase.Veto.Width.mm,
            GdmlShowCase.Veto.Thickness.mm + 10,
            GdmlShowCase.Veto.Thickness.mm,
            GdmlShowCase.Veto.LightGuide1Length.mm,
            "scintillatorLightGuide1Solid$label"
        )
        val scintillatorLightGuide2Solid = solids.trd(
            70,
            GdmlShowCase.Veto.Width.mm,
            70,
            GdmlShowCase.Veto.Thickness.mm + 10,
            GdmlShowCase.Veto.LightGuide2Length.mm,
            "scintillatorLightGuide2Solid$label"
        )
        val scintillatorLightGuideSolid = solids.union(
            scintillatorLightGuide1Solid,
            scintillatorLightGuide2Solid,
            "scintillatorLightGuideSolid$label"
        ) {
            position(z = -GdmlShowCase.Veto.LightGuide2Length.mm / 2 - GdmlShowCase.Veto.LightGuide1Length.mm / 2)
        }

        val scintillatorLightGuideVolume =
            volume(iaxoMaterials.lightGuide,
                scintillatorLightGuideSolid,
                name = "scintillatorLightGuideVolume$label")

        // this should be in a 'if (includePMT)' but we need to define an empty 'GdmlVolume' or equivalent...
        // TODO: find out how
        val scintillatorPhotomultiplierSolid = solids.tube(
            GdmlShowCase.Veto.PhotomultiplierDiameter.mm / 2,
            GdmlShowCase.Veto.PhotomultiplierLength.mm,
            "scintillatorPhotomultiplierSolid$label"
        )
        val scintillatorPhotomultiplierVolume = volume(
            iaxoMaterials.pmt,
            scintillatorPhotomultiplierSolid,
            "scintillatorPhotomultiplierVolume$label"
        )

        return assembly {
            physVolume(scintillatorVolume)
            physVolume(scintillatorWrappingVolume)
            val captureLayerYOffset =
                GdmlShowCase.Veto.Thickness.mm / 2 + GdmlShowCase.Veto.WrappingThickness.mm + GdmlShowCase.Veto.CaptureLayerThickness.mm / 2
            physVolume(captureLayerVolume) {
                position(y = captureLayerYOffset)
            }
//                    physVolume(captureLayerVolume) {
//                        position(y = -captureLayerYOffset)
//                    }
            physVolume(scintillatorLightGuideVolume) {
                position(z = -length.mm / 2 - GdmlShowCase.Veto.LightGuide1Length.mm / 2)
            }
            if (includePMT) {
                physVolume(scintillatorPhotomultiplierVolume) {
                    position(
                        z = -length.mm / 2 - GdmlShowCase.Veto.PhotomultiplierLength.mm / 2
                                - GdmlShowCase.Veto.LightGuide1Length.mm - GdmlShowCase.Veto.LightGuide2Length.mm
                    )
                }
            }
        }
    }

    fun vetoLayer(
        n: Int,
        separation: Double = GdmlShowCase.Veto.SeparationAdjacent.mm,
        length: GdmlShowCase.VetoLengths = GdmlShowCase.VetoLengths.LongSideStandard,
    ): GdmlRef<GdmlAssembly> {
        val step = GdmlShowCase.Veto.Width.mm + 2 * GdmlShowCase.Veto.WrappingThickness.mm + separation
        val offset = step * (n + 1) / 2.0
        return assembly {
            for (i in 1..n) {
                physVolume(veto(length), name = "$name.veto$i") {
                    position { x = step * i - offset }
                }
            }
        }
    };

    fun vetoGroup(): GdmlRef<GdmlAssembly> {
        // TODO: find out why this is not displayed, yet it can be seen in the ROOT event viewer and looks OK
        return assembly {
            for (i in 1..4) {
                physVolume(vetoLayer(4), name = "layer$i") {
                    position { y = (i - 1) * 70 }
                }
            }
        }
    }

    val vetoFrontLayer = assembly {
        val n = 3
        repeat(n) { j ->
            val step = GdmlShowCase.Veto.Width.mm + 2 * GdmlShowCase.Veto.WrappingThickness.mm + GdmlShowCase.Veto.SeparationAdjacent.mm
            val offset = step * (n - 1) / 2.0
            if (j == 1) {
                physVolume(veto(GdmlShowCase.VetoLengths.LongSideSmall), name = "vetoSmall$j") {
                    position {
                        x = step * j - offset
                        z = (GdmlShowCase.VetoLengths.LongSideSmall.mm - GdmlShowCase.VetoLengths.LongSideStandard.mm) / 2
                    }
                }
                // rotated
                physVolume(veto(GdmlShowCase.VetoLengths.LongSideSmall), name = "vetoSmallRotated$j") {
                    position {
                        x = step * j - offset
                        z = -(GdmlShowCase.VetoLengths.LongSideSmall.mm - GdmlShowCase.VetoLengths.LongSideStandard.mm) / 2
                    }
                    rotation(x = 180) { unit = AUnit.DEG }
                }
            } else {
                physVolume(veto(GdmlShowCase.VetoLengths.LongSideStandard), name = "veto$j") {
                    position(x = step * j - offset)
                }
            }
        }
    }

    val worldSize = 2000
    val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")

    world = volume(iaxoMaterials.world, worldBox, "world") {
        physVolume(chamberVolume, name = "Chamber")
        physVolume(detectorPipeVolume, name = "DetectorPipe") {
            position(z = GdmlShowCase.DetectorPipe.ZinWorld.mm)
        }
        physVolume(shieldingVolume, name = "Shielding")

        val yShieldingDistance =
            GdmlShowCase.Shielding.SizeXY.mm / 2 + GdmlShowCase.Shielding.EnvelopeThickness.mm + GdmlShowCase.Veto.FullThickness.mm / 2

        val zShieldingDistance =
            GdmlShowCase.Shielding.SizeZ.mm / 2 + GdmlShowCase.Shielding.EnvelopeThickness.mm + GdmlShowCase.Veto.FullThickness.mm / 2

        val nLayers = 3
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4), name = "VetoLayerTop$i") {
                position {
                    y = yShieldingDistance + (GdmlShowCase.Veto.FullThickness.mm + 20) * i + 20
                    z = -GdmlShowCase.Shielding.OffsetZ.mm
                }
                rotation { unit = AUnit.DEG; y = 180 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4), name = "VetoLayerBottom$i") {
                position {
                    y = -yShieldingDistance - (GdmlShowCase.Veto.FullThickness.mm + 0) * i - 20
                    z = -GdmlShowCase.Shielding.OffsetZ.mm
                }
                rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4), name = "VetoLayerBack$i") {
                position {
                    z = -zShieldingDistance - GdmlShowCase.Shielding.OffsetZ.mm - 130 - (GdmlShowCase.Veto.FullThickness.mm + 20) * i
                    y = 80
                }
                rotation { unit = AUnit.DEG; x = -90 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4), name = "VetoLayerEast$i") {
                position {
                    x = -yShieldingDistance - 130 - (GdmlShowCase.Veto.FullThickness.mm + 20) * i
                    y = 0
                    z = -GdmlShowCase.Shielding.OffsetZ.mm - 30
                }
                rotation { unit = AUnit.DEG; x = -90; z = 90 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4), name = "VetoLayerWest$i") {
                position {
                    x = yShieldingDistance + 130 + (GdmlShowCase.Veto.FullThickness.mm + 20) * i
                    y = 0
                    z = -GdmlShowCase.Shielding.OffsetZ.mm
                }
                rotation { unit = AUnit.DEG; x = 0; z = 90; y = 0 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoFrontLayer, name = "VetoLayerFront$i") {
                position {
                    y = 0
                    z = -GdmlShowCase.Shielding.OffsetZ.mm + zShieldingDistance + 130 + (GdmlShowCase.Veto.FullThickness.mm + 20) * i
                }
                rotation { unit = AUnit.DEG; x = -90; y = 90 }
            }
        }
    }
}
