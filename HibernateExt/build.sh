#!/bin/sh
CURDIR=`dirname $0`
HIBERNATECORE=$CURDIR/../hibernate-3.1
java -cp "$HIBERNATECORE/lib/ant-launcher-1.6.5.jar" org.apache.tools.ant.launch.Launcher -lib $HIBERNATECORE/lib $*
