#!/usr/bin/env bash

set -e

VERSION=$1
GIT_TAG=v${VERSION}

if [[ -z ${VERSION} ]]
then
    echo "Usage: $0 <VERSION>"
    exit -1
fi

mvn -Prelease nexus-staging:release

# Undo the mvn versions:set : we don't want commits for releases:
git checkout `find . -name pom.xml`
git tag ${GIT_TAG}
git push --tags

rm -f `find . -name pom.xml.versionsBackup`
