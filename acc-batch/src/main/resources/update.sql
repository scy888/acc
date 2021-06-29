update acc_repay.repay_plan set term_status='OVERDUE' where due_bill_no='YX-101' and term in (1,4);
update acc_repay.repay_plan set due_bill_no='YX-101_',term_status='UNDUE' where due_bill_no='YX-101' and term=3;
update acc_repay.repay_plan set term_status='UNDUE' where due_bill_no='YX-102' and term=2;
update acc_repay.repay_plan set term_status='OVERDUE' where due_bill_no='YX-102' and term=6;

update acc_repay.repay_summary set asset_status='SETTLED' where due_bill_no='YX-101';
update acc_repay.repay_summary set asset_status='OVERDUE' where due_bill_no='YX-102';

update acc_repay.repay_trans_flow set trans_amount=170.01 where trans_amount=170;

update acc_repay.receipt_detail set amount=180.01 where amount=180;
update acc_repay.receipt_detail set amount=90 where amount=90.1;
update acc_repay.receipt_detail set due_bill_no='YX-102_' where due_bill_no='YX-102' and amount=250;