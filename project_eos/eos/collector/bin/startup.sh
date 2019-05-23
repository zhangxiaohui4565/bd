#!/bin/bash

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set EOS_HOME if not already set
[ -z "$EOS_HOME" ] && EOS_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`
export EOS_HOME

# lib dir
LIB_DIR=$EOS_HOME/lib

# Get java
if [ "X$JAVA_HOME" == "X" ]; then
  _RUNJAVA="`which java`"
  if [ "X$_RUNJAVA" == "X" ]; then
    echo "Java is not found in PATH, and JAVA_HOME is not set"; exit 1
  fi
else
  _RUNJAVA="$JAVA_HOME/bin/java"
fi

CLASS_PATH=
for jar in `ls "$LIB_DIR"`; do
  CLASS_PATH=$CLASS_PATH:"$LIB_DIR/$jar"
done

JMX_CONFIG="-Dcom.sun.management.jmxremote"
JMX_CONFIG="$JMX_CONFIG -Dcom.sun.management.jmxremote.local.only=false"
JMX_CONFIG="$JMX_CONFIG -Dcom.sun.management.jmxremote.authenticate=false"
JMX_CONFIG="$JMX_CONFIG -Dcom.sun.management.jmxremote.ssl=false"
JMX_CONFIG="$JMX_CONFIG -Dcom.sun.management.jmxremote.port=59100"
JMX_CONFIG="$JMX_CONFIG -Dcom.sun.management.jmxremote.rmi.port=59100"
JMX_CONFIG="$JMX_CONFIG -Djava.rmi.commons.hostname=127.0.0.1"

CONFIG_FILE="$EOS_HOME/conf/eos-collector.properties"
LOG4J_FILE="$EOS_HOME/conf/log4j.properties"

HEAP_DUMP_DIR=/var/logs/elog/heap_dump
[ -e $HEAP_DUMP_DIR ] || mkdir $HEAP_DUMP_DIR

JAVA_OPTS=$(cat <<EOF
-server
-Xmx2048m -Xms2048m -Xmn1024m
-XX:+UseParallelGC -XX:ParallelGCThreads=4
-XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Xloggc:/var/logs/eos/collector-gc.log
-XX:+PrintClassHistogram
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$HEAP_DUMP_DIR
-Djava.awt.headless=true
-Djava.net.preferIPv4Stack=true
EOF
)
exec "$_RUNJAVA" $JAVA_OPTS $JACOCO_OPTS $JMX_CONFIG -cp $CLASS_PATH com.gupao.bd.eos.collector.App -c $CONFIG_FILE -l $LOG4J_FILE
