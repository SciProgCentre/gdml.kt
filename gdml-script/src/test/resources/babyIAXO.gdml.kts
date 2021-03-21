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
};
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
};
enum class Shielding(val mm: Double) {
    SizeXY(590.0), SizeZ(540.0),
    ShaftShortSideX(194.0), ShaftShortSideY(170.0),
    ShaftLongSide(340.0),
    DetectorToShieldingSeparation(-60.0), EnvelopeThickness(10.0),
    OffsetZ(DetectorToShieldingSeparation.mm + Chamber.Height.mm / 2 + Chamber.ReadoutKaptonThickness.mm + Chamber.BackplateThickness.mm),
};
// vetoes
enum class VetoLengths(val mm: Double) {
    LongSideStandard(800.0), LongSideSmall(300.0), LongSideLarge(1500.0),
};
enum class Veto(val mm: Double) {
    Width(200.0), Thickness(50.0), LongSide(VetoLengths.LongSideStandard.mm),
    CaptureLayerThickness(1.0), WrappingThickness(1.0),
    SeparationAdjacent(5.0),
    FullThickness(Thickness.mm + 2 * WrappingThickness.mm + 2 * CaptureLayerThickness.mm),

    // light guide and PMT
    LightGuide1Length(130.0), LightGuide2Length(80.0),
    PhotomultiplierLength(233.5), PhotomultiplierDiameter(70.0),
};

/*
    We pull materials from remote GitHub repository
    Currently I am organizing them into a map, but I don't think this is the best way.
    Sadly I can't use an enum here, since I need to be in the 'Gdml' context and I can't declare a class there.
    TODO: find a better way to organize materials which supports autocompletion
 */
loadMaterialsFromUrl("https://raw.githubusercontent.com/rest-for-physics/materials/4e2e72017e83ab6c2947e77f04365fbb92c42dc7/materials.xml")
val materialsMap = mapOf(
    "World" to materials.composite("G4_AIR"),
    "Gas" to materials.composite("G4_Ar"),
    "Vacuum" to materials.composite("G4_Galactic"),
    "Copper" to materials.composite("G4_Cu"),
    "Lead" to materials.composite("G4_Pb"),
    "Teflon" to materials.composite("G4_TEFLON"),
    "Kapton" to materials.composite("G4_KAPTON"),
    "Mylar" to materials.composite("G4_MYLAR"),
    "Scintillator" to materials.composite("BC408"),
    "LightGuide" to materials.composite("G4_LUCITE"),
    "NeutronCapture" to materials.composite("G4_Cd"),
    "ScintillatorWrapping" to materials.composite("G4_NEOPRENE"),
    "PMT" to materials.composite("G4_STAINLESS-STEEL"),
)

structure {
    fun chamberVolume(): GdmlRef<GdmlAssembly> {
        val chamberBodySolid = solids.subtraction(
            solids.box(
                Chamber.SquareSide.mm,
                Chamber.SquareSide.mm,
                Chamber.Height.mm,
                "chamberBodyBaseSolid"
            ), solids.tube(Chamber.Diameter.mm / 2, Chamber.Height.mm, "chamberBodyHoleSolid"),
            "chamberBodySolid"
        )
        val chamberBodyVolume = volume(materialsMap["Copper"]!!, chamberBodySolid, "chamberBodyVolume")

        val chamberBackplateSolid = solids.box(
            Chamber.SquareSide.mm,
            Chamber.SquareSide.mm,
            Chamber.BackplateThickness.mm,
            "chamberBackplateSolid"
        )
        val chamberBackplateVolume =
            volume(materialsMap["Copper"]!!, chamberBackplateSolid, "chamberBackplateVolume")

        val chamberTeflonWallSolid =
            solids.tube(Chamber.Diameter.mm / 2, Chamber.Height.mm, "chamberTeflonWallSolid") {
                rmin = Chamber.Diameter.mm / 2 - Chamber.TeflonWallThickness.mm
            }
        val chamberTeflonWallVolume =
            volume(materialsMap["Teflon"]!!, chamberTeflonWallSolid, "chamberTeflonWallVolume")
        // readout
        val kaptonReadoutSolid = solids.box(
            Chamber.SquareSide.mm, Chamber.SquareSide.mm, Chamber.ReadoutKaptonThickness.mm, "kaptonReadoutSolid"
        )
        val kaptonReadoutVolume = volume(materialsMap["Kapton"]!!, kaptonReadoutSolid, "kaptonReadoutVolume")

        val copperReadoutSolid =
            solids.box(
                Chamber.ReadoutPlaneSide.mm,
                Chamber.ReadoutPlaneSide.mm,
                Chamber.ReadoutCopperThickness.mm,
                "copperReadoutSolid"
            )
        val copperReadoutVolume = volume(materialsMap["Kapton"]!!, copperReadoutSolid, "copperReadoutVolume")

        // cathode
        val cathodeTeflonDiskBaseSolid = solids.tube(
            Chamber.SquareSide.mm / 2,
            Chamber.CathodeTeflonDiskThickness.mm,
            "cathodeTeflonDiskBaseSolid"
        ) {
            rmin = Chamber.CathodeTeflonDiskHoleRadius.mm
        }
        val cathodeCopperDiskSolid = solids.tube(
            Chamber.CathodeCopperSupportOuterRadius.mm,
            Chamber.CathodeCopperSupportThickness.mm,
            "cathodeCopperDiskSolid"
        ) {
            rmin = Chamber.CathodeCopperSupportInnerRadius.mm
        }
        val cathodeTeflonDiskSolid =
            solids.subtraction(cathodeTeflonDiskBaseSolid, cathodeCopperDiskSolid, "cathodeTeflonDiskSolid") {
                position =
                    GdmlPosition(z = -Chamber.CathodeTeflonDiskThickness.mm / 2 + Chamber.CathodeCopperSupportThickness.mm / 2)
            }

        val cathodeTeflonDiskVolume =
            volume(materialsMap["Teflon"]!!, cathodeTeflonDiskSolid, "cathodeTeflonDiskVolume") {}

        val cathodeWindowSolid =
            solids.tube(
                Chamber.CathodeTeflonDiskHoleRadius.mm,
                Chamber.CathodeWindowThickness.mm,
                "cathodeWindowSolid"
            )
        val cathodeWindowVolume = volume(materialsMap["Mylar"]!!, cathodeWindowSolid, "cathodeWindowVolume")

        // cathode copper disk pattern
        val cathodePatternLineAux = solids.box(
            Chamber.CathodePatternLineWidth.mm,
            Chamber.CathodeCopperSupportInnerRadius.mm * 2,
            Chamber.CathodeCopperSupportThickness.mm,
            "cathodePatternLineAux"
        )
        val cathodePatternCentralHole = solids.tube(
            Chamber.CathodePatternDiskRadius.mm,
            Chamber.CathodeCopperSupportThickness.mm * 1.1, "cathodePatternCentralHole"
        )
        val cathodePatternLine =
            solids.subtraction(cathodePatternLineAux, cathodePatternCentralHole, "cathodePatternLine")

        val cathodePatternDisk = solids.tube(
            Chamber.CathodePatternDiskRadius.mm,
            Chamber.CathodeCopperSupportThickness.mm, "cathodePatternDisk"
        ) { rmin = Chamber.CathodePatternDiskRadius.mm - Chamber.CathodePatternLineWidth.mm }


        var cathodeCopperDiskSolidAux: GdmlRef<GdmlUnion> = GdmlRef("")
        for (i in 0..3) {
            cathodeCopperDiskSolidAux =
                solids.union(
                    if (i > 0) cathodeCopperDiskSolidAux else cathodeCopperDiskSolid,
                    cathodePatternLine, "cathodeCopperDiskSolidAux$i"
                ) {
                    rotation = GdmlRotation(unit = AUnit.DEG, x = 0, y = 0, z = 45 * i)
                }
        }

        val cathodeCopperDiskFinal =
            solids.union(cathodeCopperDiskSolidAux, cathodePatternDisk, "cathodeCopperDiskFinal")
        val cathodeCopperDiskVolume =
            volume(materialsMap["Copper"]!!, cathodeCopperDiskFinal, "cathodeCopperDiskFinal")

        val cathodeFillingBaseSolid = solids.tube(
            Chamber.CathodeTeflonDiskHoleRadius.mm,
            Chamber.CathodeTeflonDiskThickness.mm,
            "cathodeFillingBaseSolid"
        )
        val cathodeFillingSolid =
            solids.subtraction(cathodeFillingBaseSolid, cathodeCopperDiskFinal, "cathodeFillingSolid") {
                position =
                    GdmlPosition(z = -Chamber.CathodeTeflonDiskThickness.mm / 2 + Chamber.CathodeCopperSupportThickness.mm / 2)
            }
        val cathodeFillingVolume = volume(materialsMap["Vacuum"]!!, cathodeFillingSolid, "cathodeFillingVolume") {}

        // gas
        val gasSolidOriginal = solids.tube(
            Chamber.Diameter.mm / 2 - Chamber.TeflonWallThickness.mm,
            Chamber.Height.mm, "gasSolidOriginal"
        )
        val gasSolidAux =
            solids.subtraction(gasSolidOriginal, copperReadoutSolid, "gasSolidAux") {
                position = GdmlPosition(z = -Chamber.Height.mm / 2 + Chamber.ReadoutCopperThickness.mm / 2)
                rotation = GdmlRotation(unit = AUnit.DEG, z = 45)
            }
        val gasSolid =
            solids.subtraction(gasSolidAux, cathodeWindowSolid, "gasSolid") {
                position = GdmlPosition(z = Chamber.Height.mm / 2 - Chamber.CathodeWindowThickness.mm / 2)
            }
        val gasVolume = volume(materialsMap["Gas"]!!, gasSolid, "gasVolume")

        return assembly {
            physVolume(gasVolume) {
                name = "gas"
            }
            physVolume(chamberBackplateVolume) {
                name = "chamberBackplate"
                position {
                    z =
                        -Chamber.Height.mm / 2 - Chamber.ReadoutKaptonThickness.mm - Chamber.BackplateThickness.mm / 2
                }
            }
            physVolume(chamberBodyVolume) {
                name = "chamberBody"
            }
            physVolume(chamberTeflonWallVolume) {
                name = "chamberTeflonWall"
            }
            physVolume(kaptonReadoutVolume) {
                name = "kaptonReadout"
                position { z = -Chamber.Height.mm / 2 - Chamber.ReadoutKaptonThickness.mm / 2 }
            }
            physVolume(copperReadoutVolume) {
                name = "copperReadout"
                position { z = -Chamber.Height.mm / 2 + Chamber.ReadoutCopperThickness.mm / 2 }
                rotation { unit = AUnit.DEG; z = 45 }
            }
            physVolume(cathodeWindowVolume) {
                name = "cathodeWindow"
                position { z = Chamber.Height.mm / 2 - Chamber.CathodeWindowThickness.mm / 2 }
            }

            physVolume(cathodeTeflonDiskVolume) {
                name = "cathodeTeflonDisk"
                position { z = Chamber.Height.mm / 2 + Chamber.CathodeTeflonDiskThickness.mm / 2 }
            }
            physVolume(cathodeFillingVolume) {
                name = "cathodeFilling"
                position { z = Chamber.Height.mm / 2 + Chamber.CathodeTeflonDiskThickness.mm / 2 }
            }
            physVolume(cathodeCopperDiskVolume) {
                name = "cathodeCopperDiskPattern"
                position { z = Chamber.Height.mm / 2 + Chamber.CathodeCopperSupportThickness.mm / 2 }
            }
        }
    };
    fun detectorPipeVolume(): GdmlRef<GdmlAssembly> {

        val detectorPipeChamberFlangeSolid = solids.tube(
            DetectorPipe.ChamberFlangeRadius.mm,
            DetectorPipe.ChamberFlangeThickness.mm,
            "detectorPipeChamberFlangeSolid"
        )
        val detectorPipeTelescopeFlangeSolid = solids.tube(
            DetectorPipe.TelescopeFlangeRadius.mm,
            DetectorPipe.TelescopeFlangeThickness.mm,
            "detectorPipeTelescopeFlangeSolid"
        )
        val detectorPipeSection1of2Solid =
            solids.tube(
                DetectorPipe.OuterRadius1.mm,
                DetectorPipe.Section1of2Length.mm,
                "detectorPipeSection1of2Solid"
            )
        val detectorPipeSection2of2Solid =
            solids.tube(
                DetectorPipe.OuterRadius2.mm,
                DetectorPipe.Section2of2Length.mm,
                "detectorPipeSection2of2Solid"
            )
        val detectorPipeAux1 =
            solids.union(detectorPipeChamberFlangeSolid, detectorPipeSection1of2Solid, "detectorPipeAux1")
            {
                position = GdmlPosition(z = DetectorPipe.Union1Z.mm)
            }
        val detectorPipeAux2 =
            solids.union(detectorPipeAux1, detectorPipeSection2of2Solid, "detectorPipeAux2")
            {
                position = GdmlPosition(z = DetectorPipe.Union2Z.mm)
            }
        val detectorPipeNotEmpty =
            solids.union(detectorPipeAux2, detectorPipeTelescopeFlangeSolid, "detectorPipeNotEmpty")
            {
                position = GdmlPosition(z = DetectorPipe.Union3Z.mm)
            }
        val detectorPipeInside1of3Solid = solids.tube(
            DetectorPipe.InsideSection1of3Radius.mm,
            DetectorPipe.InsideSection1of3Length.mm,
            "detectorPipeInside1of3Solid"
        )
        val detectorPipeInside2of3Solid = solids.tube(
            DetectorPipe.InsideSection2of3Radius.mm,
            DetectorPipe.InsideSection2of3Length.mm,
            "detectorPipeInside2of3Solid"
        )
        val detectorPipeInside3of3Solid = solids.tube(
            DetectorPipe.InsideSection3of3Radius.mm,
            DetectorPipe.InsideSection3of3Length.mm,
            "detectorPipeInside3of3Solid"
        )
        val detectorPipeInsideCone1of3Solid = solids.cone(
            DetectorPipe.InsideCone1of3Length.mm,
            DetectorPipe.InsideSection1of3Radius.mm, DetectorPipe.InsideSection2of3Radius.mm,
            "detectorPipeInsideCone1of3Solid"
        )
        val detectorPipeInsideCone2of3Solid = solids.cone(
            DetectorPipe.InsideCone2of3Length.mm,
            DetectorPipe.InsideSection2of3Radius.mm, DetectorPipe.InsideSection3of3Radius.mm,
            "detectorPipeInsideCone2of3Solid"
        )
        val detectorPipeInsideCone3of3Solid = solids.cone(
            DetectorPipe.InsideCone3of3Length.mm,
            DetectorPipe.InsideSection3of3Radius.mm, DetectorPipe.InsideSectionTelescopeRadius.mm,
            "detectorPipeInsideCone3of3Solid"
        )
        val detectorPipeInsideAux1 =
            solids.union(detectorPipeInside1of3Solid, detectorPipeInsideCone1of3Solid, "detectorPipeInsideAux1") {
                position = GdmlPosition(z = DetectorPipe.InsideUnion1Z.mm)
            }
        val detectorPipeInsideAux2 =
            solids.union(detectorPipeInsideAux1, detectorPipeInside2of3Solid, "detectorPipeInsideAux2") {
                position = GdmlPosition(z = DetectorPipe.InsideUnion2Z.mm)
            }
        val detectorPipeInsideAux3 =
            solids.union(detectorPipeInsideAux2, detectorPipeInsideCone2of3Solid, "detectorPipeInsideAux3") {
                position = GdmlPosition(z = DetectorPipe.InsideUnion3Z.mm)
            }
        val detectorPipeInsideAux4 =
            solids.union(detectorPipeInsideAux3, detectorPipeInside3of3Solid, "detectorPipeInsideAux4") {
                position = GdmlPosition(z = DetectorPipe.InsideUnion4Z.mm)
            }
        val detectorPipeInside =
            solids.union(detectorPipeInsideAux4, detectorPipeInsideCone3of3Solid, "detectorPipeInside") {
                position = GdmlPosition(z = DetectorPipe.InsideUnion5Z.mm)
            }
        val detectorPipeSolid = solids.subtraction(detectorPipeNotEmpty, detectorPipeInside, "detectorPipeSolid") {
            position =
                GdmlPosition(z = DetectorPipe.InsideSection1of3Length.mm / 2 - DetectorPipe.ChamberFlangeThickness.mm / 2)
        }
        val detectorPipeVolume = volume(materialsMap["Copper"]!!, detectorPipeSolid, "detectorPipeVolume")
        val detectorPipeFillingVolume =
            volume(materialsMap["Vacuum"]!!, detectorPipeInside, "detectorPipeFillingVolume")

        return assembly {
            physVolume(detectorPipeVolume) {
                name = "detectorPipe"
            }
            physVolume(detectorPipeFillingVolume) {
                name = "detectorPipeFilling"
                position { z = DetectorPipe.FillingOffsetWithPipe.mm }
            }
        }
    };
    fun shieldingVolume(): GdmlRef<GdmlAssembly> {
        val leadBoxSolid = solids.box(Shielding.SizeXY.mm, Shielding.SizeXY.mm, Shielding.SizeZ.mm, "leadBoxSolid")
        val leadBoxShaftSolid =
            solids.box(
                Shielding.ShaftShortSideX.mm,
                Shielding.ShaftShortSideY.mm,
                Shielding.ShaftLongSide.mm,
                "leadBoxShaftSolid"
            )
        val leadBoxWithShaftSolid = solids.subtraction(leadBoxSolid, leadBoxShaftSolid, "leadBoxWithShaftSolid") {
            position = GdmlPosition(z = Shielding.SizeZ.mm / 2 - Shielding.ShaftLongSide.mm / 2)
        }
        val leadShieldingVolume = volume(materialsMap["Lead"]!!, leadBoxWithShaftSolid, "ShieldingVolume")
        // lead shielding envelope
        val leadBoxEnvelopeSolid = solids.subtraction(
            solids.subtraction(
                solids.box(
                    Shielding.SizeXY.mm + 2 * Shielding.EnvelopeThickness.mm,
                    Shielding.SizeXY.mm + 2 * Shielding.EnvelopeThickness.mm,
                    Shielding.SizeZ.mm + 2 * Shielding.EnvelopeThickness.mm,
                    "leadBoxEnvelopeBase"
                ), leadBoxSolid,
                "leadBoxEnvelopeWithoutHole"
            ), solids.box(
                Shielding.ShaftShortSideX.mm,
                Shielding.ShaftShortSideY.mm,
                Shielding.EnvelopeThickness.mm,
                "leadBoxEnvelopeHole"
            ), "leadBoxEnvelope"
        ) { position = GdmlPosition(z = Shielding.SizeZ.mm / 2 + Shielding.EnvelopeThickness.mm / 2) }
        val leadBoxEnvelopeVolume = volume(materialsMap["World"]!!, leadBoxEnvelopeSolid, "leadBoxEnvelopeVolume")
        return assembly {
            physVolume(leadShieldingVolume) {
                name = "shielding20cm"
                position { z = -Shielding.OffsetZ.mm }
            }
            physVolume(leadBoxEnvelopeVolume) {
                name = "shieldingEnvelope"
                position { z = -Shielding.OffsetZ.mm }
            }
        }
    };
    fun veto(length: VetoLengths, includePMT: Boolean = false): GdmlRef<GdmlAssembly> {
        val l = "-${length.mm}mm"
        val scintillatorSolid = solids.box(
            Veto.Width.mm,
            Veto.Thickness.mm,
            length.mm,
            "scintillatorSolid$l",
        )
        val scintillatorVolume =
            volume(materialsMap["Scintillator"]!!, scintillatorSolid, "scintillatorVolume$l")

        val captureLayerSolid = solids.box(
            Veto.Width.mm,
            Veto.CaptureLayerThickness.mm,
            length.mm,
            "captureLayerSolid$l"
        )
        val captureLayerVolume = volume(materialsMap["NeutronCapture"]!!, captureLayerSolid, "captureLayerVolume")

        val scintillatorWrappingBaseSolid = solids.box(
            Veto.Width.mm + 2 * Veto.WrappingThickness.mm,
            Veto.Thickness.mm + 2 * Veto.WrappingThickness.mm,
            length.mm + 2 * Veto.WrappingThickness.mm,
            "scintillatorWrappingBaseSolid$l"
        )
        val scintillatorWrappingHoleSolid = solids.box(
            Veto.Width.mm,
            Veto.Thickness.mm,
            length.mm,
            "scintillatorWrappingHoleSolid$l",
        )
        val scintillatorWrappingFullSolid = solids.subtraction(
            scintillatorWrappingBaseSolid,
            scintillatorWrappingHoleSolid,
            "scintillatorWrappingFullSolid$l"
        )
        val scintillatorWrappingSolid =
            solids.subtraction(
                scintillatorWrappingFullSolid,
                solids.box(
                    Veto.Width.mm + 2 * Veto.WrappingThickness.mm,
                    Veto.Thickness.mm + 2 * Veto.WrappingThickness.mm,
                    Veto.WrappingThickness.mm * 1.2, // we remove a bit more, it shouldn't matter since its a sub
                    "scintillatorWrappingRemoveSideSolid"
                ), "scintillatorWrappingSolid$l"
            ) {
                position =
                    GdmlPosition(z = length.mm / 2 + Veto.WrappingThickness.mm / 2)
            }
        val scintillatorWrappingVolume =
            volume(materialsMap["ScintillatorWrapping"]!!, scintillatorWrappingSolid, "scintillatorWrappingSolid")

        // light guide and PMT
        val scintillatorLightGuide1Solid = solids.trd(
            Veto.Width.mm,
            Veto.Width.mm,
            Veto.Thickness.mm + 10,
            Veto.Thickness.mm,
            Veto.LightGuide1Length.mm,
            "scintillatorLightGuide1Solid"
        )
        val scintillatorLightGuide2Solid = solids.trd(
            70,
            Veto.Width.mm,
            70,
            Veto.Thickness.mm + 10,
            Veto.LightGuide2Length.mm,
            "scintillatorLightGuide2Solid"
        )
        val scintillatorLightGuideSolid = solids.union(
            scintillatorLightGuide1Solid,
            scintillatorLightGuide2Solid,
            "scintillatorLightGuideSolid"
        ) {
            position =
                GdmlPosition(z = -Veto.LightGuide2Length.mm / 2 - Veto.LightGuide1Length.mm / 2)
        }

        val scintillatorLightGuideVolume =
            volume(materialsMap["LightGuide"]!!, scintillatorLightGuideSolid, "scintillatorLightGuideVolume")

        // this should be in a 'if (includePMT)' but we need to define an empty 'GdmlVolume' or equivalent...
        // TODO: find out how
        val scintillatorPhotomultiplierSolid = solids.tube(
            Veto.PhotomultiplierDiameter.mm / 2,
            Veto.PhotomultiplierLength.mm,
            "scintillatorPhotomultiplierSolid"
        )
        val scintillatorPhotomultiplierVolume = volume(
            materialsMap["PMT"]!!,
            scintillatorPhotomultiplierSolid,
            "scintillatorPhotomultiplierVolume"
        )

        return assembly {
            physVolume(scintillatorVolume) {
                name = "scintillator$l"
            }
            physVolume(scintillatorWrappingVolume) {
                name = "scintillatorWrapping$l"
            }
            val captureLayerYOffset =
                Veto.Thickness.mm / 2 + Veto.WrappingThickness.mm + Veto.CaptureLayerThickness.mm / 2
            physVolume(captureLayerVolume) {
                name = "captureLayerN1$l"
                position { y = captureLayerYOffset }
            }
            physVolume(captureLayerVolume) {
                name = "captureLayerN2$l"
                position { y = -captureLayerYOffset }
            }
            physVolume(scintillatorLightGuideVolume) {
                name = "lightGuide"
                position { z = -length.mm / 2 - Veto.LightGuide1Length.mm / 2 }
            }
            if (includePMT) {
                physVolume(scintillatorPhotomultiplierVolume) {
                    name = "PMT"
                    position {
                        z =
                            -length.mm / 2 - Veto.PhotomultiplierLength.mm / 2 - Veto.LightGuide1Length.mm - Veto.LightGuide2Length.mm
                    }
                }
            }
        }
    };
    fun vetoLayer(
        n: Int,
        separation: Double = Veto.SeparationAdjacent.mm,
        length: VetoLengths = VetoLengths.LongSideStandard,
    ): GdmlRef<GdmlAssembly> {
        val step = Veto.Width.mm + 2 * Veto.WrappingThickness.mm + separation
        val offset = step * (n + 1) / 2.0
        return assembly {
            for (i in 1..n) {
                physVolume(veto(length)) {
                    name = "veto$i"
                    position { x = step * i - offset }
                }
            }
        }
    };
    fun vetoGroup(): GdmlRef<GdmlAssembly> {
        // TODO: find out why this is not displayed, yet it can be seen in the ROOT event viewer and looks OK
        return assembly {
            for (i in 1..4) {
                physVolume(vetoLayer(4)) {
                    name = "layer$i"
                    position { y = (i - 1) * 70 }
                }
            }
        }
    };

    val vetoFrontLayer = assembly {
        val n = 3
        repeat(n) { j ->
            val step = Veto.Width.mm + 2 * Veto.WrappingThickness.mm + Veto.SeparationAdjacent.mm
            val offset = step * (n - 1) / 2.0
            if (j == 1) {
                physVolume(veto(VetoLengths.LongSideSmall)) {
                    name = "vetoSmall$j"
                    position {
                        x = step * j - offset
                        z = (VetoLengths.LongSideSmall.mm - VetoLengths.LongSideStandard.mm) / 2
                    }
                }
                // rotated
                physVolume(veto(VetoLengths.LongSideSmall)) {
                    name = "vetoSmallRotated$j"
                    position {
                        x = step * j - offset
                        z = -(VetoLengths.LongSideSmall.mm - VetoLengths.LongSideStandard.mm) / 2
                    }
                    rotation { unit = AUnit.DEG; x = 180; }
                }
            } else {
                physVolume(veto(VetoLengths.LongSideStandard)) {
                    name = "veto$j"
                    position { x = step * j - offset }
                }
            }
        }
    }

    val worldSize = 2000
    val worldBox = solids.box(worldSize, worldSize, worldSize, "worldBox")
    world = volume(materialsMap["World"]!!, worldBox, "world") {
        physVolume(chamberVolume()) {
            name = "Chamber"
        }
        physVolume(detectorPipeVolume()) {
            name = "DetectorPipe"
            position { z = DetectorPipe.ZinWorld.mm }
        }
        physVolume(shieldingVolume()) {
            name = "Shielding"
        }

        val yShieldingDistance =
            Shielding.SizeXY.mm / 2 + Shielding.EnvelopeThickness.mm + Veto.FullThickness.mm / 2
        val xShieldingDistance = yShieldingDistance
        val zShieldingDistance =
            Shielding.SizeZ.mm / 2 + Shielding.EnvelopeThickness.mm + Veto.FullThickness.mm / 2

        val nLayers = 3
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4)) {
                name = "VetoLayerTop$i"
                position {
                    y = yShieldingDistance + (Veto.FullThickness.mm + 20) * i + 20
                    z = -Shielding.OffsetZ.mm
                }
                rotation { unit = AUnit.DEG; y = 180 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4)) {
                name = "VetoLayerBottom$i"
                position {
                    y = -yShieldingDistance - (Veto.FullThickness.mm + 0) * i - 20
                    z = -Shielding.OffsetZ.mm
                }
                rotation { unit = AUnit.DEG; y = 180 * (i + 1) }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4)) {
                name = "VetoLayerBack$i"
                position {
                    z = -zShieldingDistance - Shielding.OffsetZ.mm - 130 - (Veto.FullThickness.mm + 20) * i
                    y = 80
                }
                rotation { unit = AUnit.DEG; x = -90 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4)) {
                name = "VetoLayerEast$i"
                position {
                    x = -xShieldingDistance - 130 - (Veto.FullThickness.mm + 20) * i
                    y = 0
                    z = -Shielding.OffsetZ.mm - 30
                }
                rotation { unit = AUnit.DEG; x = -90; z = 90 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoLayer(4)) {
                name = "VetoLayerWest$i"
                position {
                    x = xShieldingDistance + 130 + (Veto.FullThickness.mm + 20) * i
                    y = 0
                    z = -Shielding.OffsetZ.mm
                }
                rotation { unit = AUnit.DEG; x = 0; z = 90; y = 0 }
            }
        }
        repeat(nLayers) { i ->
            physVolume(vetoFrontLayer) {
                name = "VetoLayerFront$i"
                position {
                    y = 0
                    z = -Shielding.OffsetZ.mm + zShieldingDistance + 130 + (Veto.FullThickness.mm + 20) * i
                }
                rotation { unit = AUnit.DEG; x = -90; y = 90 }
            }
        }
    }
}