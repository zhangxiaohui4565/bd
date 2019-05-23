
with RST as (

select
  U.user_id
  ,W.company as work_company
  ,W.salary as work_salary
  ,W.entrydate as work_entry_date
from user_tag_storage U
left join crm_works W
       on (U.user_id = W.accountid)
where U.tag_id = 'account_name'
)

from RST
insert overwrite table user_tag_storage
partition (tag_id='work_company')
select user_id, 'string', work_company, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='work_salary')
select user_id, 'decimal', null, null, work_salary, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='work_entry_date')
select user_id, 'date', null, work_entry_date, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

;