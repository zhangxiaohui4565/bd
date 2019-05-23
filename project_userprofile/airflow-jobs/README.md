# 选人平台ETL作业
## 环境要求
Linux: CentOS 7.4+ or Ubuntu 18+

Mac: 10.13+

Windows 10 1803+

## Python环境
Python版本：3.6(默认) 或 3.7
Mac安装:
```
brew install python
brew install pipenv
```

CentOS:
```
sudo yum install -y https://centos7.iuscommunity.org/ius-release.rpm
sudo yum install -y python36u python36u-pip python36u-devel
sudo pip3.6 install --upgrade pip
pip install pipenv --user
```
## 项目代码
推荐使用PyCharm打开本项目目录，然后需要初始化pipenv环境。

Pipfile里的Python版本是3.6, 如果Python版本不是3.6，请修改文件保证版本正确.
初始化pipenv环境，可以在shell里输入
```
export SLUGIFY_USES_TEXT_UNIDECODE=yes
pipenv sync
```
如果以上命令报错，可以尝试下面这种方法
```
export SLUGIFY_USES_TEXT_UNIDECODE=yes
pipenv install 'apache-airflow[hive]'
```
之后就可以在PyCharm里把dags目录添加成Source Folders就可以正常编译Airflow DAG代码了

## Hive环境
1. 确保beeline命令在$PATH路径下, 在shell里输入beeline无报错
2. 下载elasticsearch for hadoop: https://github.com/elastic/elasticsearch-hadoop/releases,
   并上传到HDFS:///tmp/elasticsearch-hadoop-6.4.2.jar

## 安装Airflow
1. Git Clone当前项目
2. 修改.env: 将AIRFLOW_HOME修改成项目所在目录的完整路径
3. 修改airflow.cfg: 将airflow_home, dags_folder, base_log_folder, sql_alchemy_conn, 按照项目路径做相应修改
4. 初始化pipenv环境, 在命令行中执行以下命令:

```
pipenv sync
pipenv shell

export AIRFLOW_HOME=$(pwd)

airflow initdb

airflow webserver -p 8080
airflow scheduler
```

## 配置Airflow
1. 浏览器里打开http://localhost:8080, 在UI里修改Connection的属性
连接名: hive_cli_default
Host: localhost
Port: 10000
Extra: {"use_beeline":true,"auth":"none"}

2. 新建es_default的连接
连接名Conn Id: es_default
Conn Type: HTTP
Host: es服务域名
Port: 9200

3. 修改dags/crm_user_profile_dag.py代码中es-server所表示的ES服务器域名或IP

## 启动DAG
打开user_tag_etl_dag和crm_job_executor开关

## Airflow Docker Compose 启动
由于airflow的docker镜像不包含hive的相关依赖，也就无法执行HiveOperator，所以本方案只供学习Airflow使用。

如果要在docker airflow里使用hive，需要自定义docker镜像，把hive的相关文件装到镜像里，有需要的同学可以私下联系老师。

### 安装Docker (Mac版)
```
brew install docker
brew install docker-compose
brew install xhyve
brew install docker-machine
brew install docker-machine-driver-xhyve
brew install docker-machine-nfs

docker-machine create --driver xhyve
eval $(docker-machine env)

docker-machine-nfs default

docker info
```
### 启动
执行`docker-compose up -d`

### 访问
浏览器里打开`http://<docker-machine-ip>:8080/`