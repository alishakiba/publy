# Publy - Change Log
All notable changes to this project will be documented in this file.


## [1.2.2] - 2017-10-09

### Changed
- Publy no longer overwrites generated files and linked CSS and JS files with minified versions when site minification is enabled. Instead, the minified files are placed alongside the original files with a ".min" extension. In addition, class names in the HTML and CSS files are no longer shortened.


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

<!---
Example of a more complicated release:

## [0.0.7] - 2015-02-16
### Added
- Link, and make it obvious that date format is ISO 8601.

### Changed
- Clarified the section on "Is there a standard change log format?".

### Fixed
- Fix Markdown links to tag comparison URL with footnote-style links.

### Removed
- Remove empty sections from CHANGELOG, they occupy too much space and
create too much noise in the file. People will have to assume that the
missing sections were intentionally left out because they contained no
notable changes.

--->