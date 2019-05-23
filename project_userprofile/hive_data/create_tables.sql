DROP TABLE IF EXISTS crm_accounts;
CREATE EXTERNAL TABLE crm_accounts (
    id int,
    accountname string,
    createdat string,
    invitecode string,
    isdeleted string,
    islocked string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data/accounts'
TBLPROPERTIES ('skip.header.line.count'='1')
;

DROP TABLE IF EXISTS crm_orders;
CREATE EXTERNAL TABLE crm_orders (
    id int,
    accountid int,
    orderno string,
    type string,
    createdat string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data/orders'
TBLPROPERTIES ('skip.header.line.count'='1')
;

DROP TABLE IF EXISTS crm_cards;
CREATE EXTERNAL TABLE crm_cards (
    accountid int,
    bankaccountname string,
    bankaccountno string,
    bankcode string,
    isvalid string,
    createdat string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data/cards'
TBLPROPERTIES ('skip.header.line.count'='1')
;

DROP TABLE IF EXISTS crm_loanhistories;
CREATE EXTERNAL TABLE crm_loanhistories (
    loanorderno string,
    status string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data/loanhistories'
TBLPROPERTIES ('skip.header.line.count'='1')
;

DROP TABLE IF EXISTS crm_loans;
CREATE EXTERNAL TABLE crm_loans (
    loanorderno string,
    purpose string,
    status string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data/loans'
TBLPROPERTIES ('skip.header.line.count'='1')
;

DROP TABLE IF EXISTS crm_persons;
CREATE EXTERNAL TABLE crm_persons (
    accountid int,
    birthday string,
    gender string,
    yearlyfamilyincome float,
    maritalstatus string,
    hasfillcompleted string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data/persons'
TBLPROPERTIES ('skip.header.line.count'='1')
;

DROP TABLE IF EXISTS crm_works;
CREATE EXTERNAL TABLE crm_works (
    accountid int,
    company string,
    salary float,
    entrydate string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/data/works'
TBLPROPERTIES ('skip.header.line.count'='1')
;

CREATE TABLE user_tag_storage (
    user_id int,
    tag_datatype string,
    tag_str_value string,
    tag_date_value date,
    tag_decimal_value decimal(20,4),
    tag_stat_date string,
    dw_create_time string
)
PARTITIONED BY (tag_id string)
CLUSTERED BY (user_id)
SORTED BY (user_id)
INTO 4 BUCKETS 
STORED AS PARQUET
;