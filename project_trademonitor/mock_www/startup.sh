#!/usr/bin/env bash

work=`dirname $0`
pidFile=${work}/pid

if [ ! -f ${pidFile} ];then
    echo "启动：mock_www ..."
    nohup node ${work}/mock_www.js > ${work}/log 2>&1 &

    echo $! > ${work}/pid
    echo "启动完成，pid=`cat ${pidFile}`"

    tail -100f ${work}/log
else
    echo "mock_www 已经启动,运行pid:`cat ${pidFile}`"
    exit 1
fi


