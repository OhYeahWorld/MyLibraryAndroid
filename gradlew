#!/usr/bin/env sh
# Lightweight Gradle bootstrapper for this sample project.
set -eu

GRADLE_VERSION="9.5.0"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
DIST_HOME="$GRADLE_USER_HOME/manual-wrapper/gradle-$GRADLE_VERSION"
GRADLE_BIN="$DIST_HOME/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
    mkdir -p "$GRADLE_USER_HOME/manual-wrapper"
    ZIP_FILE="$GRADLE_USER_HOME/manual-wrapper/gradle-$GRADLE_VERSION-bin.zip"
    URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    echo "Gradle $GRADLE_VERSION не найден. Загружаю дистрибутив..."
    if command -v curl >/dev/null 2>&1; then
        curl -fL "$URL" -o "$ZIP_FILE"
    elif command -v wget >/dev/null 2>&1; then
        wget -O "$ZIP_FILE" "$URL"
    else
        echo "Для первой загрузки Gradle установите curl или wget." >&2
        exit 1
    fi
    unzip -q -o "$ZIP_FILE" -d "$GRADLE_USER_HOME/manual-wrapper"
fi

exec "$GRADLE_BIN" "$@"
