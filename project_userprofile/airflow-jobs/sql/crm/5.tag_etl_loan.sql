
with RST as (
select
  U.user_id
  , Case
         When crm_orders.Id is not null Then 'Yes'
         Else 'No'
         End  as has_loan_appl
  , Case
         When crm_orders.CreatedAt is NULL then 'No'
         When crm_orders.CreatedAt = BO.FirstTime Then 'Yes'
              Else 'No'
         End as is_first_loan_appl
  , Case
         When Loans.Status in ('Funded', 'Repaid2Investor', 'Repaid2Platform') Then 'Yes'
         Else 'No'
         End as is_loan_disbursed
  , Loans.Purpose as loan_purpose
  , Case
         When LAH.initial_approved_cnt > 0 Then 'Yes'
         Else 'No'
         End as is_loan_approved

from user_tag_storage U
left join (
    select *
    from (
        select
            accountid, createdAt, id, orderno, row_number() over (partition by accountid order by createdat desc) rn
        from crm_orders
        where type = 'application'
        ) T
        where rn = 1
) crm_orders
    on U.user_id = crm_orders.accountId
left join (
    Select AccountId, min(createdAT) FirstTime  from crm_orders group by accountId
) BO
    on U.user_id = BO.accountId
left join crm_Loans Loans
    on crm_orders.OrderNo = Loans.loanOrderNo
left join (
    select
        loanOrderNo,
        sum(case when status = 'InitialApproved' then 1 else 0 end) initial_approved_cnt
    from crm_LoanHistories
    group by loanOrderNo
 ) LAH on crm_orders.OrderNo = LAH.loanOrderNo
where U.tag_id = 'account_name'
)

from RST
insert overwrite table user_tag_storage
partition (tag_id='has_loan_appl')
select user_id, 'string', has_loan_appl, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='is_first_loan_appl')
select user_id, 'string', is_first_loan_appl, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='is_loan_disbursed')
select user_id, 'string', is_loan_disbursed, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='loan_purpose')
select user_id, 'string', loan_purpose, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp

insert overwrite table user_tag_storage
partition (tag_id='is_loan_approved')
select user_id, 'string', is_loan_approved, null, null, date_add(to_date('${hiveconf:airflow.ctx.dag_run.execution_date}'), 1), current_timestamp
;
