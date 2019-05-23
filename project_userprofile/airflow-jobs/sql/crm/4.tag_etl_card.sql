
with RST as (

select
  U.user_id
  ,to_date(C.createdat) as card_bind_date
  ,C.bankAccountName as bank_account_name
  ,C.bankCode as card_bank_code
from user_tag_storage U
left join (
    -- 只选择最近绑定的卡片
    select *
    from (
        select
            accountid, createdAt, bankAccountName, bankAccountNo, bankCode,
            row_number() over (partition by accountid order by id desc) rn
        from crm_cards
        where isvalid = 1
    ) T
    where rn = 1
) C
    on (U.user_id = C.accountid)
where U.tag_id = 'account_name'
)

from RST
insert overwrite table user_tag_storage
partition (tag_id='card_bind_date')
select user_id, 'date', null, card_bind_date, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='bank_account_name')
select user_id, 'string', bank_account_name, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='card_bank_code')
select user_id, 'string', card_bank_code, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

;