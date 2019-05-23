#!/usr/bin/env bash

work=`dirname $0`
pidFile=${work}/pid

if [ ! -f ${pidFile} ];then
    echo "启动：metric_compute ..."
    nohup spark-submit --class com.gupao.bd.trademonitor.mcs.job.JobExecutor \
    ${work}/target/metric_compute-1.0.jar ${work}/conf/job.properties \
    > ${work}/log 2>&1 &

    echo $! > ${work}/pid
    echo "启动完成，pid=$!"

    tail -100f ${work}/log
else
    echo "metric_compute 已经启动,运行pid:`cat ${pidFile}`"
    exit 1
fi


