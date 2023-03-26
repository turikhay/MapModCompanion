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
if [[ "$ACTIONS_STEP_DEBUG" == "true" ]] || [[ "$DEBUG" ]]; then
  DEBUG="true"
else
  DEBUG=""
fi
if [[ "$DEBUG" ]] || [[ "$GRADLE_REBUILD" ]]; then
  (cd .. && ./gradlew build)
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

proxy_extra=""
server_extra=""

if [[ "$ACTION" == "manual" ]]; then
  proxy_extra=$(cat <<-EOF
    ports:
      - 25565:25565
EOF
)
  set +u
  if [[ "$JAVA_DEBUG" ]]; then
    echo "[NOTE] Java debugging enabled. Use 127.0.0.1:9010 for proxy, 127.0.0.1:9011 for server"
    proxy_extra+=$(cat <<-EOF

      - 9010:9001
    environment:
      - JVM_XX_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9001
EOF
)
    server_extra=$(cat <<-EOF
    ports:
      - 9011:9001
    environment:
      - JVM_XX_OPTS=-Ddisable.watchdog=true -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$([[ "$JAVA_VERSION" -lt 9 ]] && echo "9001" || echo "*:9001")
EOF
)
  fi
  set -u
fi

cat << EOF > "$OVERRIDE_FILE"
services:
  bot:
    container_name: "$TEST_CONTAINER_NAME"
  proxy:
    volumes:
      - $TEST_ENV/plugins_proxy:/server/plugins
$proxy_extra
  server:
    volumes:
      - $TEST_ENV/plugins_server:/data/plugins
$server_extra
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

function docker_compose_down() {
  docker_compose down --rmi local
}

function perform_test {
  debug_echo "Stopping old containers"
  docker_compose_down >/dev/null

  local auto=1
  if [[ "$1" == "manual" || "$1" == "debug" ]]; then
    local auto=""
  fi

  set +u
  if [[ "$PROTOCOLLIB_VERSION" ]]; then
    set -u
    local protocollib_path="$PLUGINS_SERVER/ProtocolLib.jar"
    debug_echo "Will use ProtocolLib: $protocollib_path"
    if [[ ! -f "$protocollib_path"  ]]; then
      wget "https://github.com/dmulloy2/ProtocolLib/releases/download/$PROTOCOLLIB_VERSION/ProtocolLib.jar" -O "$protocollib_path"
    fi
  fi
  set -u

  debug_echo "Starting container"
  docker_compose "up --force-recreate --build $([[ "$auto" ]] && echo "--detach" || echo "")"

  if [[ "$auto" ]]; then
    trap docker_compose_down SIGINT

    if [[ $DEBUG ]]; then
      docker compose logs -f &
    else
      docker compose logs -f bot &
    fi

    debug_echo "Waiting for bot container to exit: $TEST_CONTAINER_NAME"
    TEST_EXIT=`docker wait "$TEST_CONTAINER_NAME"`
    debug_echo "Done: $TEST_EXIT"

    docker_compose_down >/dev/null 2>/dev/null

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
    echo "[ACTION] Performing automatic test; version: $VERSION, proxy: $PROXY_TYPE"
    perform_test "auto"
    ;;

  manual)
    echo "[ACTION] Spinning up the server for manual test; version: $VERSION, proxy: $PROXY_TYPE"
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
