#/bin/sh

echo "--------Sync Mysql to HDFS--------"

hadoop fs -test -d /user/bill/opencart_db/oc_product_description

if [ $? == 0 ]; then
    hadoop fs -rm -r /user/bill/opencart_db/oc_product_description
fi

sqoop import --connect jdbc:mysql://172.19.19.122/opencart \
--username root --password pwd \
--table oc_product_description \
--columns "product_id,name" \
--target-dir /user/bill/opencart_db/oc_product_description \
--m 1

if [ $? -ne 0 ]; then
    echo "--------Sync Mysql to HDFS Error--------"
    exit 1
else
    echo "--------Sync MySql to HDFS Success--------"
fi

echo "--------Compute PV--------"

hadoop fs -test -d /user/bill/opencart/pv

if [ $? == 0 ]; then
    hadoop fs -rm -r /user/bill/opencart/pv
fi

spark-submit --master yarn-client --class com.gupao.bigdata.spark.demo.ComputePV metriccount_spark-1.0-SNAPSHOT.jar /user/bill/opencart_nginx_log/* /user/bill/opencart_db/oc_product_description/* /user/bill/opencart/pv

if [ $? -ne 0 ]; then
    echo "--------Compute PV Error--------"
    exit 1
else
    echo "--------Compute PV Success--------"
fi

echo "--------Compute UV--------"

hadoop fs -test -d /user/bill/opencart/uv

if [ $? == 0 ]; then
    hadoop fs -rm -r /user/bill/opencart/uv
fi

spark-submit --master yarn-client --class com.gupao.bigdata.spark.demo.ComputeUV metriccount_spark-1.0-SNAPSHOT.jar /user/bill/opencart_nginx_log/* /user/bill/opencart_db/oc_product_description/* /user/bill/opencart/uv

if [ $? -ne 0 ]; then
    echo "--------Compute UV Error--------"
    exit 2
else
    echo "--------Compute UV Success--------"
fi

echo "--------Compute Visit Duration--------"

hadoop fs -test -d /user/bill/opencart/visit_duration

if [ $? == 0 ]; then
    hadoop fs -rm -r /user/bill/opencart/visit_duration
fi

spark-submit --master yarn-client --class com.gupao.bigdata.spark.demo.PageVisitDuration metriccount_spark-1.0-SNAPSHOT.jar /user/bill/opencart_nginx_log/* /user/bill/opencart_db/oc_product_description/* /user/bill/opencart/visit_duration

if [ $? -ne 0 ]; then
    echo "--------Compute Visit Duration Error--------"
    exit 3
else
    echo "--------Compute Visit Duration Success--------"
fi

echo "--------Sync PV to Mysql--------"

sqoop export --connect jdbc:mysql://172.19.19.122/opencart_stat \
--username root --password pwd \
--table pv_stat -m 1 \
--columns "product_id,name,value" \
--export-dir /user/bill/opencart/pv \
--update-key "product_id" \
--update-mode allowinsert \
--fields-terminated-by '\t'

if [ $? -ne 0 ]; then
    echo "--------Sync PV to Mysql Error--------"
    exit 4
else
    echo "--------Sync PV to Mysql Success--------"
fi

echo "--------Sync UV to Mysql--------"

sqoop export --connect jdbc:mysql://172.19.19.122/opencart_stat \
--username root --password pwd \
--table uv_stat -m 1 \
--columns "product_id,name,value" \
--export-dir /user/bill/opencart/uv \
--update-key "product_id" \
--update-mode allowinsert \
--fields-terminated-by '\t'

if [ $? -ne 0 ]; then
    echo "--------Sync UV to Mysql Error--------"
    exit 5
else
    echo "--------Sync UV to Mysql Success--------"
fi

echo "--------Sync Duration to Mysql--------"

sqoop export --connect jdbc:mysql://172.19.19.122/opencart_stat \
--username root --password pwd \
--table duration_stat -m 1 \
--export-dir /user/bill/opencart/visit_duration \
--columns "product_id,name,value" \
--update-key "product_id" \
--update-mode allowinsert \
--fields-terminated-by '\t'

if [ $? -ne 0 ]; then
    echo "--------Sync Duration to Mysql Error--------"
    exit 6
else
    echo "--------Sync Duration to Mysql Success--------"
fi