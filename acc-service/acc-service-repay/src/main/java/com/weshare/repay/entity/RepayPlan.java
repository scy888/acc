package com.weshare.repay.entity;

import com.weshare.service.api.enums.TermPaidOutTypeEnum;
import com.weshare.service.api.enums.TermStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 还款计划表 5*
 *
 * @author qujiayuan
 * @since 2020.07.10
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@javax.persistence.Table(indexes = {
        @Index(name = "idx_repay_plan_due_bill_no", columnList = "dueBillNo, term", unique = true),
        @Index(name = "idx_repay_plan_batch_date", columnList = "batchDate"),
        @Index(name = "idx_repay_plan_project_no", columnList = "projectNo")
})
@Table(appliesTo = "repay_plan", comment = "还款计划表")
public class RepayPlan {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(50) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "int(3) not null comment '期次'")
    private Integer term;

    @Column(columnDefinition = "varchar(20) null comment '项目编号'")
    private String projectNo;

    @Column(columnDefinition = "varchar(20) null comment '产品编号'")
    private String productNo;

    @Column(columnDefinition = "varchar(20) not null comment '本期还款状态 TermStatusEnum' ")
    @Enumerated(EnumType.STRING)
    private TermStatusEnum termStatus;

    @Column(columnDefinition = "date null comment '计息开始日'")
    private LocalDate termStartDate;

    @Column(columnDefinition = "date not null comment '应还款日'")
    private LocalDate termDueDate;

    @Column(columnDefinition = "date null comment '本期次还清的日期' ")
    private LocalDate repayDate;

    @Column(columnDefinition = "varchar(20) null comment '本期还款的还清类型 TermPaidOutTypeEnum' ")
    @Enumerated(EnumType.STRING)
    private TermPaidOutTypeEnum termPaidOutType;

    @Column(columnDefinition = "decimal(12,2) not null comment '本期账单应还金额(元)'")
    private BigDecimal termBillAmount;

    // 应还明细项
    @Column(columnDefinition = "decimal(12,2) not null comment '应还本金(元)'")
    private BigDecimal termPrin;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还利息(元)'")
    private BigDecimal termInt;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还罚息(元)'")
    private BigDecimal termPenalty;

    // 实还明细项

    @Column(columnDefinition = "decimal(12,2) not null comment '已还本金(元)'")
    private BigDecimal termRepayPrin;

    @Column(columnDefinition = "decimal(12,2) not null comment '已还利息(元)'")
    private BigDecimal termRepayInt;

    @Column(columnDefinition = "decimal(12,2) not null comment '已还罚息(元)'")
    private BigDecimal termRepayPenalty;

    // 减免明细
    @Column(columnDefinition = "decimal(12,2) not null comment '减免利息(元)'")
    private BigDecimal termReduceInt;

    @Column(columnDefinition = "date null comment '批次日期' ")
    private LocalDate batchDate;

    @Column(columnDefinition = "varchar(50) null comment '备注'")
    private String remark;

    private LocalDateTime createdDate;

    @Column(length = 100)
    private String createdBy;

    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;

}
