#!/bin/bash

# Take a version number as parameter



# Verify that this is being run on the release branch
branch="$(hg branch)"

if [ ! $branch = "release" ]
then
  echo "Current branch is \"$branch\". To use this script, switch to the release branch with \"hg update release\", merge all the necessary changes from the main branch, then run this script again."
  exit 2
done


# Make sure the version number in UIConstants.java matches the given version number
codeMajorVersion="$(grep MAJOR_VERSION \"src/publy/gui/UIConstants.java\")"
codeMinorVersion="$(grep MINOR_VERSION \"src/publy/gui/UIConstants.java\")"

#TODO: check that this is the right line
#TODO: extract the version number
#TODO: compare the version numbers




# Clean and build


# Make the .jar file executable (see issue #201)


# Remove the old zip file
rm Publy*.zip


# Create a zip file called Publy X.Y.zip, containing:
#   LICENSE
#   NOTICE
#   Some readme file
#   publications.bib
#   Publy.jar
#   data/*
#   lib/*

# Tag the current revision with the release number
hg tag #TODO