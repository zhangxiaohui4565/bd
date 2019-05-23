#!/usr/bin/env bash

module=`dirname $0`

scp -c aes128-cbc ${module}/target/demo_kudu-1.0-SNAPSHOT.jar root@gp-bd-master01:/home/gupao/project/demo_kudu


