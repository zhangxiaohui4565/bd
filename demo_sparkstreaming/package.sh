#!/usr/bin/env bash

work=`dirname $0`

mvn -f ${work}/../pom.xml -Dmaven.test.skip=true clean package -pl demo_sparkstreaming -am






