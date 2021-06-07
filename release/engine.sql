select version(); -- 查看数据库的版本号
show table status; -- 查看所有表状态
show table status from acc_loan where name='link_man'; -- 产看某张表的状态
alter table acc_loan.link_man engine innodb; -- 修改表的储存引擎
alter table acc_batch.tb_person add column create_date datetime comment '创建时间';
alter table acc_batch.tb_person drop column create_date;
alter table acc_loan.loan_contract add loan_status_enum varchar(10) null comment '放款状态' after repay_day;
alter table acc_loan.loan_contract modify loan_status_enum varchar(10) not null comment '放款状态';

-- netstat -nao findstr 9002



UPDATE acc_repay.receipt_detail a ,acc_loan.loan_contract b
 set a.total_term=b.total_term,a.last_modified_date=now()
  where a.project_no='WS121212' and a.due_bill_no=b.due_bill_no;

select m.due_bill_no,m.sum_repay_amount,n.sum_amount from
(select due_bill_no, sum(term_repay_int+term_repay_prin+term_repay_penalty+term_reduce_int) sum_repay_amount from acc_repay.repay_plan where project_no='WS121212' group by due_bill_no) m left join
(select due_bill_no,sum(amount) sum_amount from acc_repay.receipt_detail where project_no='WS121212' group by due_bill_no) n on m.due_bill_no=n.due_bill_no where m.sum_repay_amount!=n.sum_amount or n.due_bill_no is null
UNION
select n.due_bill_no,m.sum_repay_amount,n.sum_amount from
(select due_bill_no, sum(term_repay_int+term_repay_prin+term_repay_penalty+term_reduce_int) sum_repay_amount from acc_repay.repay_plan where project_no='WS121212' group by due_bill_no) m right join
(select due_bill_no,sum(amount) sum_amount from acc_repay.receipt_detail where project_no='WS121212' group by due_bill_no) n on m.due_bill_no=n.due_bill_no where m.sum_repay_amount!=n.sum_amount or m.due_bill_no is null

select n.due_bill_no,m.sum_trans_amount,n.sum_amount from
(select due_bill_no,sum(trans_amount) sum_trans_amount from acc_repay.repay_trans_flow where project_no='WS121212' group by due_bill_no) m
right join
(select due_bill_no,sum(amount) sum_amount from acc_repay.receipt_detail where project_no='WS121212' group by due_bill_no) n
on m.due_bill_no=n.due_bill_no where m.sum_trans_amount!=n.sum_amount or m.due_bill_no is null;
UNION
select m.due_bill_no,m.sum_trans_amount,n.sum_amount from
(select due_bill_no,sum(trans_amount) sum_trans_amount from acc_repay.repay_trans_flow where project_no='WS121212' group by due_bill_no) m
left join
(select due_bill_no,sum(amount) sum_amount from acc_repay.receipt_detail where project_no='WS121212' group by due_bill_no) n
on m.due_bill_no=n.due_bill_no where m.sum_trans_amount!=n.sum_amount or n.due_bill_no is null;

select m.due_bill_no,m.contract_amount,n.due_bill_no,n.sum_term_prin from
(select a.due_bill_no,a.contract_amount from acc_loan.loan_contract a where a.project_no='WS121212' order by a.due_bill_no desc) m ,
(select b.due_bill_no,sum(b.term_prin) sum_term_prin from acc_repay.repay_plan b where b.project_no='WS121212' group by b.due_bill_no order by b.due_bill_no) n where m.due_bill_no=n.due_bill_no and m.contract_amount!=n.sum_term_prin;

select m.due_bill_no,m.contract_amount,n.due_bill_no,n.sum_term_prin from
(select a.due_bill_no,a.contract_amount from acc_loan.loan_contract a where a.project_no='WS121212' order by a.due_bill_no desc) m right JOIN
(select b.due_bill_no,sum(b.term_prin) sum_term_prin from acc_repay.repay_plan b where b.project_no='WS121212' group by b.due_bill_no order by b.due_bill_no) n on m.due_bill_no=n.due_bill_no where m.contract_amount!=n.sum_term_prin or m.due_bill_no is null;

select * from acc_loan.loan_contract a where a.due_bill_no in (select distinct b.due_bill_no from acc_repay.repay_plan b where b.project_no='WS121212') and a.project_no='WS121212';
select * from acc_loan.loan_contract a where exists (select distinct b.due_bill_no from acc_repay.repay_plan b where b.project_no='WS121212' and a.due_bill_no=b.due_bill_no) and a.project_no='WS121212';