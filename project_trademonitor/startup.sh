#!/usr/bin/env bash

work=`dirname $0`
modules="monitor_service metric_compute order_collection mock_www"

for module in ${modules};
do
  ${work}/${module}/startup.sh
  sleep 1s
done

