#!/bin/sh

cd ~/Documents/player
echo "Building..."
mvn install > build.log
rows=`tput lines`
cols=`tput cols`
echo "Launching with size $rows x $cols ..."
#stty raw
java -classpath 'target/classes:lib/*' org.melocine.App $cols $rows $1 2>debug.log
stty cooked
