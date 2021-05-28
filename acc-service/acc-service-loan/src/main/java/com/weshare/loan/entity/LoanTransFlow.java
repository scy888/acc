package com.weshare.loan.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 放款交易流水，4*
 */
@Data
@Accessors(chain = true)
@Entity
@javax.persistence.Table(indexes = {
        @Index(name = "idx_loan_trans_flow_due_bill_no_batch_date", unique = true, columnList = "dueBillNo,batchDate"),
})
@Table(appliesTo = "loan_trans_flow", comment = "放款流水记录")
public class LoanTransFlow {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(50) null comment '流水号' ")
    private String flowSn;

    @Column(columnDefinition = "varchar(32) null comment '项目编号' ")
    private String projectNo;

    @Column(columnDefinition = "varchar(32) null comment '产品编号' ")
    private String productNo;

    @Column(columnDefinition = "varchar(50) not null comment '借据单号' ")
    private String dueBillNo;

    @Column(columnDefinition = "decimal(12,2) not null comment '金额'")
    private BigDecimal transAmount;

    @Column(columnDefinition = "varchar(50) null comment '银行账户名称'")
    private String bankAccountName;

    @Column(columnDefinition = "varchar(32) null comment '银行卡号'")
    private String bankAccountNo;

    @Column(columnDefinition = "datetime null comment '交易时间'")
    private LocalDateTime transTime;

    @Column(columnDefinition = "varchar(100) comment '交易备注'")
    private String remark;

    @Column(columnDefinition = "date null comment '批量时间'")
    private LocalDate batchDate;

    @Column(columnDefinition = "datetime null comment '创建时间'")
    private LocalDateTime createdDate;

    @Column(columnDefinition = "datetime null comment '修改时间'")
    private LocalDateTime lastModifiedDate;
}
