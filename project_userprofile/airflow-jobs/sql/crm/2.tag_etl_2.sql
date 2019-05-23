
with RST as (

select
  U.user_id
  ,P.birthday
  ,P.gender
  ,P.yearlyfamilyincome as family_income
  ,P.maritalstatus as marital_status
  ,P.hasfillcompleted as has_fill_completed
from user_tag_storage U
left join crm_persons P
       on (U.user_id = P.accountid)
where U.tag_id = 'account_name'
)

from RST
insert overwrite table user_tag_storage
partition (tag_id='birthday')
select user_id, 'date', null, birthday, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='gender')
select user_id, 'string', gender, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='family_income')
select user_id, 'decimal', null, null, family_income, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='marital_status')
select user_id, 'string', marital_status, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='has_fill_completed')
select user_id, 'string', has_fill_completed, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

;