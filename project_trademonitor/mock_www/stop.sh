#!/usr/bin/env bash

work=`dirname $0`
pidFile=${work}/pid

if [ -f ${pidFile} ];then
    echo "停止：mock_www ..."
    pid=`cat ${pidFile}`
    kill -9 ${pid}
    rm ${pidFile}
    echo "停止完成，pid=${pid}"
else
    echo "mock_www 已经停止"
    exit 1
fi




