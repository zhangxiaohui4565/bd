

with RST as (
select
  id as user_id
  ,to_date(createdat) as register_date
  ,inviteCode as invite_code
  ,accountName account_name
from crm_accounts
where
      isdeleted = 0
  and islocked = 0
)

from RST
insert overwrite table user_tag_storage
partition (tag_id='register_date')
select user_id, 'date', null, register_date, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='invite_code')
select user_id, 'string', invite_code, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='account_name')
select user_id, 'string', account_name, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp
;
