#!/usr/bin/env bash

work=`dirname $0`
pidFile=${work}/pid

if [ ! -f ${pidFile} ];then
    echo "启动：monitor_service ..."
    nohup node ${work}/monitor.js \
    > ${work}/log 2>&1 &

    echo $! > ${work}/pid
    echo "启动完成，pid=$!"

    tail -100f ${work}/log
else
    echo "monitor_service 已经启动,运行pid:`cat ${pidFile}`"
    exit 1
fi


