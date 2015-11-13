#!/bin/bash

VERSION_FILE="src/publy/Constants.java"
JAR_FILE="dist/Publy.jar"
PROJECT_DIR=${PWD##*/}


# Take a version number as parameter
: ${1?"Usage: bash $0 NEW_VERSION_NUMBER"}
version=$1


# Verify that this is being run on the release branch
branch="$(hg branch)"

if [ ! $branch = "release" ]
then
  echo "Current branch is \"$branch\". To use this script, switch to the release branch with \"hg update release\", merge all the necessary changes from the main branch, clean and build in NetBeans, then run this script again."
  exit 1
fi


# Make sure the version number in UIConstants.java matches the given version number
versionRegex="_VERSION ?= ?([0-9][0-9]*);"

codeMajorVersionLine="$(grep MAJOR_VERSION $VERSION_FILE)"
[[ $codeMajorVersionLine =~ $versionRegex ]]
codeMajorVersion="${BASH_REMATCH[1]}"

codeMinorVersionLine="$(grep MINOR_VERSION $VERSION_FILE)"
[[ $codeMinorVersionLine =~ $versionRegex ]]
codeMinorVersion="${BASH_REMATCH[1]}"

codeVersion="${codeMajorVersion}.${codeMinorVersion}"

if [ ! $version = $codeVersion ]
then
  echo "ERROR: Version in $VERSION_FILE does not match new version ($codeVersion vs $version)."
  exit 1
fi


# Make the .jar file executable
if [ ! -e "$JAR_FILE" ]
then
  echo "ERROR: Jar file $JAR_FILE is missing. Run a clean and build operation in NetBeans to generate this file."
  exit 1
fi

chmod +x "$JAR_FILE"


# Remove the old zip file
echo -n "Removing old zip file... "
rm Publy*.zip
echo "done."


# Create a new zip file
echo -n "Creating new zip file... "
ZIP_FILE="Publy $version.zip"
zip "$ZIP_FILE" "LICENSE" "NOTICE" "publications.bib"
zip -r "$ZIP_FILE" data lib # Recursively add these directories
zip -j "$ZIP_FILE" "$JAR_FILE" # Add the jar file without directory information
echo "done."


# Commit these changes
echo -n "Committing changes... "
hg commit -A -m "Updated release zip."


# Tag the current revision with the release number
hg tag "v$version"
echo "done."


