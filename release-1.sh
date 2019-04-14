#!/usr/bin/env bash

set -e

VERSION=$1

if [[ -z ${VERSION} ]]
then
    echo "Usage: $0 <VERSION>"
    exit -1
fi

mvn versions:set -DnewVersion=${VERSION}

mvn clean install

mvn -Prelease clean deploy

echo "Please verify the deployment at https://oss.sonatype.org/"
echo "If not ok, you can abort using:"
echo "  mvn -Prelease nexus-staging:drop"
echo "If ok, finalize the release with:"
echo "  ./release-2.sh ${VERSION}"
