# Publy - Change Log
All notable changes to this project will be documented in this file.


## [1.3] - 2017-10-21

### Removed
- Publy no longer tries to minimize the output files.


## [1.2.1] - 2017-08-05

### Fixed
- Added missing manifest to the release JAR.


## [1.2] - 2017-06-20
Bumped the target Java version to Java 8. If you have trouble running this version (or get an error including "Unsupported major.minor version 52.0"), please [download the latest version of Java](https://www.java.com/).

### Added
- Site minification using [MiniWeb](https://bitbucket.org/Mangara/miniweb). This will result in smaller file sizes and bandwidth savings. If you really want to, you can turn it off in the Files settings tab. (Issue #227)
- Warnings when the BibTeX contains `<abbr>` instead of `<<abbr>>`, where `abbr` is a valid abbreviation for Publy. This is most likely a mistake, but these were silently being included as weird HTML-tags. (Issue #244)

### Changed
- When the title is set to link to the paper, but there's no associated file, it might link to other fields such as the DOI or arXiv version instead. (Issue #188)

### Fixed
- Improved parser error messages. (Issues #241 and #242)
- Updated the MathJax code in DefaultHeader.html. MathJax shut down their own CDN, so this is necessary for mathematics to properly render.


## [1.1] - 2016-06-11
### Added
- The ability to add per-publication images (Issue #198).
- Optimized JS code placement.
- Option to disable the automatic opening of the generated webpage (Issue #237).

### Fixed
- Line counts in parser error messages (Issue #238).
- A bug where braces after ^ and _ were discarded in abstracts, resulting in incorrect display of complex exponents or subscripts (Issue #240).


## [1.0] - 2015-11-16
First feature-complete release.
