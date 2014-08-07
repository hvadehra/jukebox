#!/bin/sh

cd ~/Documents/player
echo "Building..."
mvn install > build.log
echo "Launching..."
stty raw
java -classpath 'target/classes:lib/*' org.melocine.App $1 2>debug.log
stty cooked
