package com.weshare.loan.entity;

import com.weshare.service.api.enums.LoanStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 借款合同表 5*
 *
 * @author jiancai.zhou
 * @date 2020-07-14 14:17
 **/
@Data
@Entity
@Accessors(chain = true)
@Table(appliesTo = "loan_contract", comment = "借款合同表")
@javax.persistence.Table(indexes = {
        @Index(name = "idx_loan_contract_due_bill_no", unique = true, columnList = "dueBillNo"),
        @Index(name = "idx_loan_contract_batch_date", columnList = "batchDate")
})
public class LoanContract {
    @Id
    private String id;

    @Column(columnDefinition = "varchar(100) not null comment '借据号' ")
    private String dueBillNo;

    @Column(columnDefinition = "varchar(50) null comment '用户编号' ")
    private String userId;

    @Column(columnDefinition = "varchar(32) null comment '项目编号' ")
    private String projectNo;

    @Column(columnDefinition = "varchar(32) null comment '产品编号' ")
    private String productNo;

    @Column(columnDefinition = "varchar(32) null comment '产品名称' ")
    private String productName;

    @Column(columnDefinition = "decimal(12,2) not null  comment '贷款金额'")
    private BigDecimal contractAmount;

    @Column(columnDefinition = "decimal(14,8) not null  comment '贷款利率'")
    private BigDecimal interestRate;

    @Column(columnDefinition = "int(3) not null comment '贷款总期数'")
    private Integer totalTerm;

    @Column(columnDefinition = "int(4) null comment '每月还款日'")
    private Integer repayDay;

    @Column(columnDefinition = "varchar(10) null comment '贷款状态'")
    @Enumerated(EnumType.STRING)
    private LoanStatusEnum loanStatusEnum;

    @Column(columnDefinition = "date null comment '第一期应还日期' ")
    private LocalDate firstTermDueDate;

    @Column(columnDefinition = "date null comment '最后一期应还日期' ")
    private LocalDate lastTermDueDate;

    @Column(columnDefinition = "decimal(12,2) null comment '本金(元)'")
    private BigDecimal principal;

    @Column(columnDefinition = "decimal(12,2) null comment '利息(元)'")
    private BigDecimal interest;

    @Column(columnDefinition = "date null comment '批次日期' ")
    private LocalDate batchDate;

    @Column(columnDefinition = "varchar(100) null comment '备注' ")
    private String remark;

    @Column(columnDefinition = "datetime null comment '创建日期' ")
    private LocalDateTime createdDate;

    @Column(columnDefinition = "datetime null comment '修改日期' ")
    private LocalDateTime lastModifiedDate;

}
