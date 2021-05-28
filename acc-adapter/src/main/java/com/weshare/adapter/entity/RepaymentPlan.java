package com.weshare.adapter.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(indexes = {
        @Index(name = "repayment_plan_due_bill_no_and_term", unique = true, columnList = "dueBillNo,term")
})
@org.hibernate.annotations.Table(appliesTo = "repayment_plan",comment = "还款计划表")
public class RepaymentPlan  {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(32) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "int not null comment '期数'")
    private Integer term;

    @Column(columnDefinition = "date not null comment '还款日'")
    private LocalDate repaymentDate;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还月供(元)'")
    private BigDecimal shouldMonthMoney;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还本金(元)'")
    private BigDecimal shouldCapitalMoney;

    @Column(columnDefinition = "decimal(12,2) not null comment '应还利息(元)'")
    private BigDecimal shouldInterestMoney;

    @Column(columnDefinition = "date null comment '跑批日期'")
    private LocalDate batchDate;

    @Column(columnDefinition = "datetime null comment '创建时间'")
    private LocalDateTime createdDate;

    @Column(columnDefinition = "datetime null comment '修改时间'")
    private LocalDateTime lastModifiedDate;
}
