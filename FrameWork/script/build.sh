#!/bin/bash

BASE_DIR="/home/andriantsoa/Documents/WorkSpace-Servlet/Split/FrameWork"
SRC_DIR="$BASE_DIR/src/main/java"
LIB_JAR="$BASE_DIR/lib/servlet-api.jar"
BIN_DIR="$BASE_DIR/bin"
DIST_DIR="$BASE_DIR/dist"
JAR_NAME="ProcessRequest.jar" # Correction de la petite faute de frappe "Preocess"

rm -rf "$BIN_DIR" "$DIST_DIR"
mkdir -p "$BIN_DIR"
mkdir -p "$DIST_DIR"

echo "-> Compilation des sources Java..."
# MODIFICATION ICI : On utilise $(find ...) pour lister et compiler TOUS les fichiers .java du projet
javac -cp "$LIB_JAR" -d "$BIN_DIR" $(find "$SRC_DIR" -name "*.java")

if [ $? -ne 0 ]; then
    echo "[Erreur] La compilation a échoué."
    exit 1
fi

echo "-> Création du fichier JAR..."
jar -cf "$DIST_DIR/$JAR_NAME" -C "$BIN_DIR" .

echo "-> Votre fichier JAR est disponible ici : $DIST_DIR/$JAR_NAME"