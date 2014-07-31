#!/bin/sh

cd ~/Documents/player
java -classpath 'target/classes:lib/*' org.melocine.App $1 2>debug.log