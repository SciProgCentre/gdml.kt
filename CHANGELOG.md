# Changelog

## [Unreleased]
### Added

### Changed
- Upgrade to Kotlin 1.8.0 and Gradle 0.7.6

### Deprecated

### Removed

### Fixed

### Security
## [0.4.0]
### Added
- Scripting support
- GdmlShowcase singleton for test purposes
- `Gdml.decodeFromUrl` method to fetch data from url
- Automatic naming of all gdml builder nodes for absent names
- Returned position/rotation/firstPosition/firstRotation builders to boolean solids
- Interfaces for components registries
- Convenient group builder
- Basic NCName validation
- Preprocessor in common

### Changed
- Refactor define builders to place the name in the last position
- `Gdml.format` and module made internal
- Container `getMember` renamed to `getItem`
- Structure builders return GdmlRef instead of elements
- `world` variable accepts ref instead of volume
- All `Gdml` builder functions now have `name` in last position and nullable to allow for automatic naming.
- physVolume position/rotation builders replaced by functions

### Deprecated
- Define builders in boolean solids.

### Removed

### Fixed
- flush after stream write
- Default units. MM for LUnit and Rad for AUnit according to https://www.star.bnl.gov/public/comp/simu/GDML/V1.0/Geometry.html#element_position_Link02EA7560
- Geant incompatibilities #33, #34 and #35

### Security

## [0.3.2]

### Changed
- Used LUnit/AUnit for serialization instead of strings

## [0.3.0]
### Added
- ZPlane builders for plyhedra and polycone
- GDML builders for all solid primitives

### Changed
- GDML builders return refs instead of actual nodes

## [0.2.0]
### Added
- Equality for gdml nodes
- Material builders
- Preprocessor for Gdml expressions (JVM only)

### Changed
- **Group name changed to `space.kscience`**
- **GDML changed to Gdml to adhere to the code style**
- Kotlin 1.4.30
- Kotlinx-serialization 1.1.0
- XmlUtil 0.81.0

