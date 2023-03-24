#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

SELF=$(SELF=$(dirname "$0") && bash -c "cd \"$SELF\" && pwd")

PROXY_TYPE="$1"
VERSION="$2"
ACTION="$3"

JAR="$SELF/../packages/single/build/libs/MapModCompanion.jar"
NAME="mmc_test_"$PROXY_TYPE"_"$VERSION
TEST_CONTAINER_NAME="$NAME"
TEST_ENV="$SELF/test_env/$NAME"
PLUGINS_SERVER="$TEST_ENV/plugins_server"
PLUGINS_PROXY="$TEST_ENV/plugins_proxy"
VERSION_INFO_FILE_NAME="docker-compose.version_info.yml"
VERSION_INFO_FILE="$TEST_ENV/$VERSION_INFO_FILE_NAME"
VERSION_INFO_SOURCE="$SELF/versions/$VERSION.sh"
OVERRIDE_FILE_NAME="docker-compose.override.yml"
OVERRIDE_FILE="$TEST_ENV/$OVERRIDE_FILE_NAME"

set +u
if [ "$ACTIONS_STEP_DEBUG" == "true" ]; then
  DEBUG="true"
else
  DEBUG=""
fi
set -u

function debug_echo {
  if [[ $DEBUG ]]; then
    echo $@
  fi
}

debug_echo "Reading $VERSION_INFO_SOURCE"
source "$VERSION_INFO_SOURCE"

debug_echo "Working in: $TEST_ENV"
rm -rf "$TEST_ENV"
mkdir -p "$TEST_ENV"

debug_echo "Populating server plugins directory"
rm -rf "$PLUGINS_SERVER"
mkdir "$PLUGINS_SERVER"
cp -r "$JAR" "$PLUGINS_SERVER/"
cp -r "$SELF/server/plugins/"* "$PLUGINS_SERVER/"

debug_echo "Populating proxy plugins directory"
rm -rf "$PLUGINS_PROXY"
mkdir -p "$PLUGINS_PROXY"
cp -r "$JAR" "$PLUGINS_PROXY/"
cp -r "$SELF/proxy/$PROXY_TYPE/plugins/"* "$PLUGINS_PROXY/"

debug_echo "Writing $VERSION_INFO_FILE_NAME"

cat << EOF > "$VERSION_INFO_FILE"
services:
  bot:
    environment:
      - BOT_VERSION=$CLIENT_VERSION
  server:
    build:
      args:
        - TAG=java$JAVA_VERSION
      tags:
        - "mmc-e2e-server:java$JAVA_VERSION"
    environment:
      - VERSION=$SERVER_VERSION

EOF

debug_echo "Writing $OVERRIDE_FILE_NAME"
cat << EOF > "$OVERRIDE_FILE"
services:
  bot:
    container_name: "$TEST_CONTAINER_NAME"
  proxy:
    volumes:
      - $TEST_ENV/plugins_proxy:/server/plugins
    $([[ "$ACTION" == "manual" ]] && echo "ports: [\"25565:25565\"]" || echo "")
  server:
    volumes:
      - $TEST_ENV/plugins_server:/data/plugins
EOF

FILES=(
    "$SELF/docker-compose.yml"
    "$SELF/docker-compose.$PROXY_TYPE.yml"
    "$VERSION_INFO_FILE"
    "$OVERRIDE_FILE"
)

FILES_FMT=`printf ' -f %s' "${FILES[@]}"`

function docker_compose {
  bash -c "docker compose$FILES_FMT $@"
}

function perform_stop() {
  docker_compose down
}

function perform_test {
  debug_echo "Stopping old containers"
  docker_compose down >/dev/null 2>/dev/null

  debug_echo "Starting container"

  local auto=1
  [[ "$1" == "manual" ]] && local auto=""

  docker_compose "up --force-recreate $([[ "$auto" ]] && echo "--detach" || echo "--build")"

  if [[ "$auto" ]]; then
    trap perform_stop SIGINT

    if [[ $DEBUG ]]; then
      docker compose logs -f &
    else
      docker compose logs -f bot &
    fi

    debug_echo "Waiting for bot container to exit: $TEST_CONTAINER_NAME"
    TEST_EXIT=`docker wait "$TEST_CONTAINER_NAME"`
    debug_echo "Done: $TEST_EXIT"

    docker_compose down >/dev/null 2>/dev/null

    if [[ "$TEST_EXIT" != "0" ]]; then
      echo "⚠️  Test failed" >&2
      exit 1
    fi
  fi
}

case $ACTION in
  convert)
    echo "[ACTION] Convert"
    docker_compose convert
    ;;

  build)
    echo "[ACTION] Building"
    docker_compose build
    ;;

  test)
    echo "[ACTION] Performing test; version: $VERSION; proxy: $PROXY_TYPE"
    perform_test "auto"
    ;;

  manual)
    echo "[ACTION] Starting manual test; version: $VERSION; proxy: $PROXY_TYPE"
    perform_test "manual"
    ;;

  cleanup)
    echo "[ACTION] Cleaning up"
    docker_compose "down --remove-orphans --rmi all -v"
    ;;

  *)
    echo "Unknown action"
    exit 1
    ;;
esac
