# CRM Job管理API

## 安装
1. 先安装好python3.7和pipenv
2. 然后执行pipenv sync

## 启动API服务
执行pipenv run python app.py

## Job API - CURL命令整理

### 删除Job
`curl -X DELETE http://localhost:5000/jobs/gkky2GYBXCk3tflewxh2`

### 查询Job
`curl http://localhost:5000/jobs/gkky2GYBXCk3tflewxh2`

### 新建Job
`curl http://localhost:5000/jobs -d 'name=crm tag4&schedule=once&query=query&status=active'`
`curl http://localhost:5000/jobs -d 'name=crm daily job&schedule=6&query=query&status=active'`

### 查询 Job List
`curl http://localhost:5000/jobs`
