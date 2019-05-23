#!/usr/bin/env bash

work=`dirname $0`
dist="gupao@gp-bd-master01:/home/gupao/wuchen/demo/sparkstreaming"

scp -oCiphers=+aes128-cbc ${work}/target/demo_sparkstreaming-1.0-SNAPSHOT.jar ${dist}

scp -oCiphers=+aes128-cbc ${work}/run_yarn.sh ${dist}







