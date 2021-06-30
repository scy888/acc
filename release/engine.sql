select version(); -- 查看数据库的版本号
show table status; -- 查看所有表状态
show table status from acc_loan where name='link_man'; -- 产看某张表的状态

alter table acc_loan.link_man engine innodb; -- 修改表的储存引擎
alter table acc_batch.tb_person add column create_date datetime comment '创建时间';
alter table acc_batch.tb_person drop column create_date;
alter table acc_loan.loan_contract add loan_status_enum varchar(10) null comment '放款状态' after repay_day;
alter table acc_loan.loan_contract modify loan_status_enum varchar(10) not null comment '放款状态';
drop table if exists acc_repay.repay_plan_back;
create unique index tb_person_name_index on tb_person(name);
drop index tb_person_name_index on tb_person;
alter table tb_person add unique index tb_person_status (status);
alter table tb_person drop index tb_person_status;

-- netstat -nao findstr 9002
select datediff(a.repay_date,a.term_due_date) from acc_repay.repay_plan a where a.due_bill_no='YX-102' and a.term=2
select year(now())-year(substr('422202199109091016',7,8)) 年龄;
select year('2021/12/12')-year(substr('422202199109091016',7,8)) 年龄;
select year(now())-substr('422202199109091016',7,4) 年龄;
select sum(a.contract_amount) 放款总额度,count(*) 放款总笔数,sum(a.contract_amount)/count(*) 平均额度,
sum(a.contract_amount)/(datediff(now(),'2020-05-15')-1) 日均放款额度 from acc_loan.loan_contract a
where a.project_no='WS121212';

SELECT replace(a.project_no,'WS','') FROM acc_repay.repay_plan a;

create table acc_repay.repay_plan_back like acc_repay.repay_plan;
insert into acc_repay.repay_plan_back select * from acc_repay.repay_plan;

SELECT * FROM AA a where a.id not in (select b.id from bb b );
SELECT * FROM AA a where a.id in (select b.id from bb b );
select * from aa a where not EXISTS (select b.id from bb b where a.id=b.id)
select * from aa a where EXISTS (select b.id from bb b where a.id=b.id)

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

select m.due_bill_no,m.sum_repay_amount,n.sum_amount from
(select due_bill_no, sum(term_repay_int+term_repay_prin+term_repay_penalty+term_reduce_int) sum_repay_amount from acc_repay.repay_plan where project_no='WS121212' group by due_bill_no) m left join
(select due_bill_no,sum(amount) sum_amount from acc_repay.receipt_detail where project_no='WS121212' group by due_bill_no) n on m.due_bill_no=n.due_bill_no where m.sum_repay_amount!=ifnull(n.sum_amount,0);

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

insert into acc_adapter.bb (
id,
b_name,
b_age,
b_address
)
select id,a_name,a_age,a_address from acc_adapter.aa limit 1;

insert into acc_adapter.bb (
id,
b_name,
b_age,
b_address
)
select 4,'周芷若',18,'峨嵋3' from dual
where not exists (select * from acc_adapter.bb where b_name='周芷若' and b_address='峨嵋3')

insert into acc_adapter.bb(
id,
b_name,
b_age,
b_address
)
values (7,'周芷若',18,'峨嵋18')
on duplicate key update b_address='峨嵋65';