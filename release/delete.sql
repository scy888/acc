
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

-- 校验用户还款主信息表放款金额是否等于还款计划表应还本金之和
select m.due_bill_no,m.sum_term_prin,n.contract_amount from
(select b.due_bill_no,sum(b.term_prin) sum_term_prin from acc_repay.repay_plan b where b.project_no='WS121212' group by b.due_bill_no) m
left join
(select a.due_bill_no,a.contract_amount from acc_repay.repay_summary a where a.project_no='WS121212') n
on m.due_bill_no=n.due_bill_no where m.sum_term_prin!=ifnull(n.contract_amount,0);

-- 校验用户还款主信息表剩余本金是否等于还款计划表剩余本金之和
select m.due_bill_no,m.sum_remain_prin,n.remain_principal from
(select b.due_bill_no,sum(b.term_prin - b.term_repay_prin) sum_remain_prin from acc_repay.repay_plan b where b.project_no='WS121212' group by b.due_bill_no) m
left join
(select a.due_bill_no,a.remain_principal from acc_repay.repay_summary a where a.project_no='WS121212') n
on m.due_bill_no=n.due_bill_no where m.sum_remain_prin!=n.remain_principal or n.due_bill_no is null;

-- 校验还款计划的已还金额是否等于实还和还款流水的扣款金额
select k.due_bill_no,k.sum_actual_repay,m.sum_trans_amount,n.sum_amount from
(select a.due_bill_no,sum(a.term_repay_prin+a.term_repay_int+a.term_repay_penalty+a.term_reduce_int) sum_actual_repay from acc_repay.repay_plan a where a.project_no='WS121212' group by a.due_bill_no) k
left join
(select b.due_bill_no,sum(b.trans_amount) sum_trans_amount from acc_repay.repay_trans_flow b where b.project_no='WS121212' group by b.due_bill_no) m
on k.due_bill_no=m.due_bill_no
left join
(select c.due_bill_no,sum(c.amount) sum_amount from acc_repay.receipt_detail c where c.project_no='WS121212' group by c.due_bill_no) n
on k.due_bill_no=n.due_bill_no
where (k.sum_actual_repay!=ifnull(m.sum_trans_amount,0) or k.sum_actual_repay!=ifnull(n.sum_amount,0));

-- 校验还款流水表和实还表的流水号是否一致
select m.due_bill_no,m.flow_sn,m.trans_amount,n.sum_amount from
(select a.due_bill_no,a.flow_sn,a.trans_amount from acc_repay.repay_trans_flow a where a.project_no='WS121212' group by a.due_bill_no,a.flow_sn) m,
(select b.due_bill_no,b.flow_sn,sum(b.amount) sum_amount from acc_repay.receipt_detail b where b.project_no='WS121212' group by b.due_bill_no,b.flow_sn) n
where m.due_bill_no=n.due_bill_no and m.flow_sn=n.flow_sn and m.trans_amount!=n.sum_amount;

-- 校验用户还款主信息表资产状态为NORMAL时,还款计划表期次状态不能有OVERDUE或全为REPAID
select m.due_bill_no,m.term_status,n.asset_status from
(select b.due_bill_no,b.term_status from acc_repay.repay_plan b where b.term_status='OVERDUE' and b.project_no='WS121212' group by b.due_bill_no) m
left join
(select a.due_bill_no,a.asset_status from acc_repay.repay_summary a where a.asset_status='NORMAL' and project_no='WS121212') n
on m.due_bill_no=n.due_bill_no
UNION
select m.due_bill_no,m.term_status,n.asset_status from
(select b.due_bill_no,b.term_status,count(*) count_count from acc_repay.repay_plan b where b.term_status='REPAID'and b.project_no='WS121212' group by b.due_bill_no) m
left join
(select a.due_bill_no,a.asset_status,a.total_term from acc_repay.repay_summary a where a.asset_status='NORMAL' and a.project_no='WS121212') n
on m.due_bill_no=n.due_bill_no where m.count_count=n.total_term;

-- 校验还款主信息表资产状态为OVERDUE时,还款计划表期次状态至少有一个为OVERDUE
select m.due_bill_no,m.total_term,n.count_count from
(select a.due_bill_no,a.asset_status,a.total_term from acc_repay.repay_summary a where a.asset_status='OVERDUE' and a.project_no='WS121212') m,
(select b.due_bill_no,b.term_status,count(*) count_count from acc_repay.repay_plan b where b.term_status!='OVERDUE' and b.project_no='WS121212' group by b.due_bill_no) n
 where m.due_bill_no=n.due_bill_no and m.total_term=n.count_count;

select m.due_bill_no,m.asset_status,n.due_bill_no,n.term_status from
(select a.due_bill_no,a.asset_status from acc_repay.repay_summary a where a.asset_status='OVERDUE' and a.project_no='WS121212') m
left join
(select b.due_bill_no,b.term_status,count(*) from acc_repay.repay_plan b where b.term_status='OVERDUE' and b.project_no='WS121212' group by b.due_bill_no) n on m.due_bill_no=n.due_bill_no where n.due_bill_no is null;

-- 校验还款主信息资产状态为SETTLED时,还款主信息表的期次状态全为REPAID
select m.due_bill_no,m.total_term,n.count_count from
(select a.due_bill_no,a.asset_status,a.total_term from acc_repay.repay_summary a where a.asset_status='SETTLED' and a.project_no='WS121212') m,
(select b.due_bill_no,b.term_status,count(*) count_count from acc_repay.repay_plan b where b.term_status='REPAID' and b.project_no='WS121212' group by b.due_bill_no) n
where m.due_bill_no=n.due_bill_no and m.total_term!=n.count_count;

-- 校验还款计划表的min(overdue_term)!=max(repaid_term)+1
select m.due_bill_no,m.overdue_min_term,n.repaid_max_term from
(select a.due_bill_no,a.term_status,min(a.term) overdue_min_term from acc_repay.repay_plan a where a.term_status='OVERDUE'and a.project_no='WS121212' group by a.due_bill_no) m,
(select b.due_bill_no,b.term_status,max(b.term) repaid_max_term from acc_repay.repay_plan b where b.term_status='REPAID' and b.project_no='WS121212' group by b.due_bill_no) n
where m.due_bill_no=n.due_bill_no and m.overdue_min_term!=n.repaid_max_term+1;

-- 校验还款计划表的min(undue_term)!=max(max(repaid_term),max(overdue_term))+1
select k.due_bill_no,k.undue_min_term,m.repaid_max_term,n.overdue_max_term from
(select a.due_bill_no,a.term_status,min(a.term) undue_min_term from acc_repay.repay_plan a where a.term_status='UNDUE' and a.project_no='WS121212' group by a.due_bill_no) k
left join
(select a.due_bill_no,a.term_status,max(a.term) repaid_max_term from acc_repay.repay_plan a where a.term_status='REPAID' and a.project_no='WS121212' group by a.due_bill_no) m
on k.due_bill_no=m.due_bill_no
left join
(select a.due_bill_no,a.term_status,max(a.term) overdue_max_term from acc_repay.repay_plan a where a.term_status='OVERDUE' and a.project_no='WS121212' group by a.due_bill_no) n
 on k.due_bill_no=n.due_bill_no where k.undue_min_term!=greatest(ifnull(m.repaid_max_term,0),ifnull(n.overdue_max_term,0))+1;

 -- 校验还款主信息表的期次是否等于还款计划的的总期次
 select m.due_bill_no,m.count_count,a.total_term from acc_repay.repay_summary a
 right join
 (select b.due_bill_no,count(*) count_count from acc_repay.repay_plan b where b.project_no='WS121212' group by b.due_bill_no) m
 on a.project_no='WS121212'and m.due_bill_no=a.due_bill_no where m.count_count!=ifnull(a.total_term,0);