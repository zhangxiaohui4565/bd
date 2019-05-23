from __future__ import print_function
from datetime import timedelta
import datetime as dt
import airflow
from airflow.models import Variable
from airflow.operators.python_operator import PythonOperator
from gupao.hooks.es_hook import ESHook
import json

import logging
import pendulum

local_tz = pendulum.timezone("Asia/Shanghai")

logging.basicConfig()

default_args = {
    'owner': 'feng',
    'start_date': dt.datetime(2018, 11, 1, tzinfo=local_tz),
    'email': ['someone@yeah.net'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 2,
    'retry_delay': timedelta(minutes=5)
}

PARAMS = {
}

def execute_job_instances_func():
    es_hook = ESHook(http_conn_id='es_default')
    print('查询待执行的实例')
    res = es_hook.search('job-instance/doc', {"query": {"match_all": {}}})
    print(res)

    for job_instance_hit in res['hits']['hits']:
        job_instance_id = job_instance_hit['_id']
        job_instance = job_instance_hit['_source']
        job_id = job_instance['job_id']
        status = job_instance['status']
        start_time = dt.datetime.strptime(job_instance['start_time'], '%Y-%m-%dT%H:%M:%S.%f')
        schedule = job_instance['schedule']
        query = job_instance['query']
        query_obj = json.loads(query)
        print(status)
        if status == 'active':
            timezone_diff = dt.timedelta(hours=8)
            print(dt.datetime.now() + timezone_diff > start_time)
            if dt.datetime.now() + timezone_diff > start_time:
                print('执行实例: {}'.format(job_instance_id))
                query_res = es_hook.search('user_profile_demo/_doc', query_obj)
                print(query_res)
                selected_group = []
                for selected_user in query_res['hits']['hits']:
                    user_id = selected_user['_source']['user_id']
                    group_item = {
                        'user_id': user_id,
                        'job_id': job_id,
                        'job_instance_id': job_instance_id,
                        'created_at': dt.datetime.now() + timezone_diff
                    }
                    selected_group.append(group_item)

                print(selected_group)
                es_hook.bulk_index('people_selection_result', '_doc', selected_group)

                # 更新实例状态
                print('更新实例状态')
                job_instance_update = job_instance.copy()
                job_instance_update['status'] = 'done'
                update_res = es_hook.update('job-instance/doc', job_instance_id, job_instance_update)
                print(update_res)

                if schedule != 'once':
                    # TODO:创建明天的任务实例
                    pass


# 主流程
dag = airflow.DAG(
    'crm_job_executor',
    schedule_interval=None,#"0 * * * *",
    dagrun_timeout=timedelta(minutes=60),
    default_args=default_args,
    catchup=False,
    max_active_runs=1)

execute_job_instances = PythonOperator(
    task_id='execute_job_instances',
    python_callable=execute_job_instances_func,
    dag=dag
)

if __name__ == "__main__":
    dag.cli()
