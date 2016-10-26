# Publy - Change Log
All notable changes to this project will be documented in this file.

## [1.2] - ????-??-??
Bumped the target Java version to Java 8. If you have trouble running this version (or get an error including "Unsupported major.minor version 52.0"), please [download the latest version of Java](https://www.java.com/).

### Added
- Site minification using [MiniWeb](https://bitbucket.org/Mangara/miniweb). This will result in smaller file sizes and bandwidth savings. If you really want to, you can turn it off in the Files settings tab.
- Warnings when the BibTeX contains `<abbr>` instead of `<<abbr>>`, where `abbr` is a valid abbreviation for Publy. This is most likely a mistake, but these were silently being included as weird HTML-tags.

### Fixed
- Improved parser error messages.

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