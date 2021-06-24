
delete from acc_adapter.loan_detail;
delete from acc_adapter.repayment_plan;
delete from acc_adapter.repayment_detail;
delete from acc_adapter.refund_ticket;
delete from acc_adapter.reback_detail;

delete from acc_loan.loan_contract;
delete from acc_loan.loan_trans_flow;

delete from acc_repay.repay_plan;
delete from acc_repay.repay_summary;
delete from acc_repay.repay_trans_flow;
delete from acc_repay.receipt_detail;

alter table acc_repay.repay_summary_back modify id int;
alter table acc_repay.repay_summary_back drop index idx_repay_summary_due_bill_no,
add unique index idx_repay_summary_due_bill_no_project_no (due_bill_no,project_no);
alter table acc_repay.repay_summary_back drop primary key,add primary key (id,project_no)
insert into acc_repay.repay_summary_back select * from acc_repay.repay_summary limit 1;

select * from acc_repay.repay_summary_back a order by cast(a.id AS SIGNED) limit 100;
select * from acc_repay.repay_summary_back a ORDER BY convert(a.due_bill_no,signed) limit 100;

-- 要分区的表中有唯一索引,要和被分区的字段组成唯一索引
alter table acc_repay.repay_summary_back drop index idx_repay_summary_due_bill_no,
add unique index idx_repay_summary_due_bill_no_project_no (due_bill_no,project_no)
-- 被分区的字段要和主键组成联合主键
alter table acc_repay.repay_summary_back drop primary key,add primary key (id,project_no);
-- 创建分区,被分区的字段每一个取值要建立对应的分区
alter table acc_repay.repay_summary_back
PARTITION by list columns (project_no) (
PARTITION part_WS121212 values in ('WS121212'),
PARTITION part_WS121213 values in ('WS121213'),
PARTITION part_WS121214 values in ('WS121214'),
PARTITION part_WS121215 values in ('WS121215'),
PARTITION part_WS121216 values in ('WS121216')
);
-- 新增一个分区
alter table acc_repay.repay_summary_back add PARTITION(PARTITION part_WS121217 values in ("WS121217"));
-- 删除分区数据
alter table acc_repay.repay_summary_back truncate PARTITION part_WS121216,part_WS121217;
--产看分区情况
select * from information_schema.partitions where table_schema='acc_repay' and table_name='repay_summary_back';