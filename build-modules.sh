#!/bin/bash
# usage : ./build-module.sh <module name>
set -e

echo building $(echo $MODULES_TO_BUILD | sed 's; ;, ;g')

# Derive GAMA p2 version from branch name: GAMA_YYYY-MM → YYYY.MM
BRANCH="${GITHUB_REF_NAME:-$(git rev-parse --abbrev-ref HEAD)}"
if [[ "$BRANCH" =~ GAMA_([0-9]{4}-[0-9]{2}) ]]; then
    GAMA_P2_VERSION="${BASH_REMATCH[1]}"
    echo "Branch ${BRANCH} → gama.p2.version=${GAMA_P2_VERSION}"
else
    echo "ERROR: branch '${BRANCH}' does not match GAMA_YYYY-MM"
    exit 1
fi

MODULES_TO_BUILD=$(echo $MODULES_TO_BUILD | sed 's; ; -pl ../;g')

ROOT=$(dirname "${BASH_SOURCE[0]}")

cd "${ROOT}/gama.plugin.parent"
mvn clean install -pl ../${MODULES_TO_BUILD} -pl ../gama.plugin.p2updatesite \
    -Dmaven.build.cache.configPath=maven-build-cache-config.xml \
    -B -e -Dgama.p2.version="${GAMA_P2_VERSION}" \
    -Ddeploy.subdir="${PLUGIN_REPO_NAME}" \
    -Dtycho.p2.transport.min-cache-minutes=0 \
    -Dtycho.equinox.resolver.uses=true \
    -P p2Repo \
    --settings ../settings.xml
