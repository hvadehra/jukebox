#!/bin/sh

cd ~/Documents/player
stty raw
java -classpath 'target/classes:lib/*' org.melocine.App $1 2>debug.log
stty cooked
