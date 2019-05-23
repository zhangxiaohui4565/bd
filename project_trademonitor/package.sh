#!/usr/bin/env bash

work=`dirname $0`

mvn -f ${work}/pom.xml -Dmaven.test.skip=true clean package

buildDir="${work}/build"
javaModules="metric_compute  order_collection"
nodeModules="mock_www  monitor_service"

if [ -d "${buildDir}" ]; then
  echo "rm dir:${buildDir}"
  rm -rf ${buildDir}
fi

for i in ${javaModules};
do
   echo "cp ${i} ..."
   mkdir -p ${buildDir}/${i}/conf
   mkdir -p ${buildDir}/${i}/target
   cp -r ${work}/${i}/conf/* ${work}/build/${i}/conf
   cp -r ${work}/${i}/target/${i}-1.0.jar ${work}/build/${i}/target
   cp -r ${work}/${i}/*.sh ${work}/build/${i}
done

for j in ${nodeModules};
do
   echo "cp ${j} ..."
   mkdir -p ${buildDir}/${j}
   cp -r ${work}/${j}/* ${work}/build/${j}
done

cp ${work}/startup.sh ${buildDir}
cp ${work}/stop.sh ${buildDir}
cp ${work}/README.md ${buildDir}

echo " build done!"

