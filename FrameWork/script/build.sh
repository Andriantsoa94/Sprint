#!/bin/bash

BASE_DIR="/home/andriantsoa/Documents/WorkSpace-Servlet/Split/FrameWork"
SRC_DIR="$BASE_DIR/src/main/java"
LIB_JAR="$BASE_DIR/lib/servlet-api.jar"
BIN_DIR="$BASE_DIR/bin"
DIST_DIR="$BASE_DIR/dist"
JAR_NAME="framework.jar"

rm -rf "$BIN_DIR" "$DIST_DIR"
mkdir -p "$BIN_DIR"
mkdir -p "$DIST_DIR"

javac -cp "$LIB_JAR" -d "$BIN_DIR" "$SRC_DIR/FrameWork.java"

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation ! Le script s'arrête."
    exit 1
fi

jar -cf "$DIST_DIR/$JAR_NAME" -C "$BIN_DIR" .

echo "-> Votre fichier JAR est disponible ici : $DIST_DIR/$JAR_NAME"
