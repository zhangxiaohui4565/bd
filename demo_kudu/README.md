#1、程序打包

运行./package.sh，打包后的脚本位于./target/demo_kudu-1.0-SNAPSHOT.jar


#2、运行demo

java -Dkudu.master=gp-bd-master01:7051  -jar ./target/demo_kudu-1.0-SNAPSHOT.jar

java -Dkudu.master=gp-bd-master01:7051  -jar  /home/gupao/project/demo_kudu/demo_kudu-1.0-SNAPSHOT.jar