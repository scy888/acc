select version(); -- 查看数据库的版本号
show table status; -- 查看所有表状态
show table status from acc_loan where name='link_man'; -- 产看某张表的状态
alter table acc_loan.link_man engine innodb; -- 修改表的储存引擎
alter table acc_batch.tb_person add column create_date datetime comment '创建时间';
alter table acc_batch.tb_person drop column create_date;