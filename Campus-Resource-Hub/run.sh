#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" campus.Main
