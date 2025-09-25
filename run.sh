#!/bin/bash

# ==========================================================
# msgpingpong Runner Java 21 (No Maven)
# ----------------------------------------------------------
# This script compiles and runs the messaging game in two modes:
#   1. single - Single-process mode (in-memory Mediator)
#   2. server - Start PlayerServer (multi-process via socket)
#   3. client - Start PlayerClient (multi-process via socket)
#
# Usage:
#   ./run.sh single    -> Run with InMemoryCommunication
#   ./run.sh server    -> Start socket server (PlayerServer)
#   ./run.sh client    -> Start socket client (PlayerClient)
# ==========================================================

MODE=$1

if [ -z "$MODE" ]; then
  echo "Usage: ./run.sh [single|server|client]"
  exit 1
fi

# Ensure Java compiler is installed
if ! command -v javac &> /dev/null; then
  echo "javac not found. Please install Java JDK 21 or later first."
  exit 1
fi

# Set source and output directories
SRC_DIR="src"
OUT_DIR="target/classes"

# Clean previous build
echo "Cleaning previous build..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Find and compile all Java files
echo "Compiling Java sources..."
find "$SRC_DIR" -name "*.java" > sources.txt
javac -d "$OUT_DIR" @sources.txt

if [ $? -ne 0 ]; then
  echo "Compilation failed. Please check your Java code."
  rm -f sources.txt
  exit 1
fi

# Clean up the sources list file
rm -f sources.txt

echo "Build successful."

# Run in single-process (in-memory mediator)
if [ "$MODE" == "single" ]; then
  echo "Starting game in SINGLE-PROCESS mode..."
  java -cp "$OUT_DIR" org.msgpingpong.core.GameController
fi

# Run in multi-process (server)
if [ "$MODE" == "server" ]; then
  echo "Starting game in MULTI-PROCESS SERVER mode..."
  java -cp "$OUT_DIR" org.msgpingpong.core.PlayerServer
fi

# Run in multi-process (client)
if [ "$MODE" == "client" ]; then
  echo "Starting game in MULTI-PROCESS CLIENT mode..."
  java -cp "$OUT_DIR" org.msgpingpong.core.PlayerClient
fi