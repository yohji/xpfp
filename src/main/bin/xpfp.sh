#!/bin/bash

#
# @author Marco Merli
# @since 1.0
#

JAVA=$(which java)

XPFP_HOME=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
XPFP_LIB="$XPFP_HOME/lib"
XPFP_JAVAOPT="$JAVA_OPTS"

$JAVA $XPFP_JAVAOPT \
	-classpath "$XPFP_LIB/*" \
	-jar $XPFP_LIB/xpfp.jar &

disown
