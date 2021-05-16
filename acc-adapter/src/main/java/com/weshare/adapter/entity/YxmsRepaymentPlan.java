package com.weshare.adapter.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
        @Index(name = "yxms_repayment_plan_due_bill_no_and_term", unique = true, columnList = "dueBillNo,term")
})
@org.hibernate.annotations.Table(appliesTo = "yxms_repayment_plan",comment = "还款计划表")
public class YxmsRepaymentPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(10) not null comment '对账日期'")
    private String reconciliationDate;

    @Column(columnDefinition = "varchar(32) not null comment '订单号'")
    private String orderNumber;

    @Column(columnDefinition = "varchar(32) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "varchar(2) not null comment '期数'")
    private String term;

    @Column(columnDefinition = "datetime not null comment '还款日'")
    private LocalDateTime repaymentDate;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还月供(元)'")
    private BigDecimal shouldMonthMoney;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还本金(元)'")
    private BigDecimal shouldCapitalMoney;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还利息(元)'")
    private BigDecimal shouldInterestlMoney;

    @Column(columnDefinition = "date null comment '跑批日期'")
    private LocalDate batchDate;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
