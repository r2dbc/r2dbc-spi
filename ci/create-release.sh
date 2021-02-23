#!/bin/bash

set -euo pipefail

#
#
# Usage: ci/create-release 201 0.9.0.RELEASE 0.9.1.BUILD-SNAPSHOT
#
# 1st arg = github issue number
# 2nd arg = release version
# 3rd arg = snapshot version after release
#
#

ISSUE=$1
RELEASE=$2
SNAPSHOT=$3

# Bump up the version in pom.xml to the desired version and commit the change
./mvnw versions:set versions:commit -DgenerateBackupPoms=false -DnewVersion=$RELEASE 
git add .
git commit -F- <<EOF
Release ${RELEASE}.

[resolves #$ISSUE]
EOF

# Tag the release
git tag -s v$RELEASE -m "${RELEASE}"

# Bump up the version in pom.xml to the next snapshot
./mvnw versions:set versions:commit -DgenerateBackupPoms=false -DnewVersion=$SNAPSHOT 
git add .
git commit -F- <<EOF
Prepare next development iteration.

[resolves #$ISSUE]
EOF