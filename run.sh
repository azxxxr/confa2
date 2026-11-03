#!/bin/sh
set -e
mkdir -p out/main
javac --release 17 -encoding UTF-8 -d out/main src/depviz/*.java
exec java -cp out/main depviz.Main "$@"
