
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