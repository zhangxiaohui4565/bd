#!/usr/bin/env bash

work=`dirname $0`
pidFile=${work}/pid

if [ -f ${pidFile} ];then
    echo "停止：order_collection ..."
    pid=`cat ${pidFile}`
    kill -9 ${pid}
    rm ${pidFile}
    echo "停止完成，pid=${pid}"
else
    echo "order_collection 已经停止"
    exit 1
fi




