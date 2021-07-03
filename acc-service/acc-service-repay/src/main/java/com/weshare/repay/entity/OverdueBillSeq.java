package com.weshare.repay.entity;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@javax.persistence.Table(indexes = {@Index(name = "idx_overdue_bill_due_bill_no_and_overdue_order", unique = true, columnList = "dueBillNo, overdueOrder")})
@Table(appliesTo = "overdue_bill_seq", comment = "资金端借据分次逾期信息(按次统计，多次逾期存多条记录)")
@Accessors(chain = true)
public class OverdueBillSeq {

    @Id
    private Long id;

    @Column(columnDefinition = "varchar(32) null comment '项目编号'")
    private String projectNo;

    @Column(columnDefinition = "varchar(32) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "date null comment '跑批业务日期'")
    private LocalDate batchDate;

    @Column(columnDefinition = "int null comment '第几次逾期'")
    private Integer overdueOrder;

    @Column(columnDefinition = "varchar(32) null comment '本次逾期状态'")
    @Enumerated(EnumType.STRING)
    private OverdueStatusEnum overdueStatus;

    @Column(columnDefinition = "date null comment '本次逾期的开始日期（DPD）'")
    private LocalDate thisTimeDpdOverdueStartDate;

    @Column(columnDefinition = "int null comment '本次逾期的天数（DPD）'")
    private Integer thisTimeDpdOverdueDays;

    @Column(columnDefinition = "varchar(100) null comment '本次逾期的期次（DPD，多期逾期则逗号相连）'")
    private String thisTimeDpdOverdueTerms;


    @Column(columnDefinition = "decimal(12,2)  null comment '本次逾期的本金'")
    private BigDecimal thisTimeDpdOverduePrincipal;

    @Column(columnDefinition = "decimal(12,2) null comment '本次逾期的利息'")
    private BigDecimal thisTimeDpdOverdueInterest;

    @Column(columnDefinition = "decimal(12,2) null comment '本次逾期的费用'")
    private BigDecimal thisTimeDpdOverdueFee;

    @Column(columnDefinition = "decimal(12,2) null comment '本次逾期的罚息'")
    private BigDecimal thisTimeDpdOverduePenalty;

    @Column(columnDefinition = "date null comment '本次逾期的开始日期（CPD）'")
    private LocalDate thisTimeCpdOverdueStartDate;

    @Column(columnDefinition = "int null comment '本次逾期的天数（CPD）'")
    private Integer thisTimeCpdOverdueDays;

    @Column(columnDefinition = "varchar(100) null comment '本次逾期的期次（CPD，多期逾期则逗号相连）'")
    private String thisTimeCpdOverdueTerms;

    @Column(columnDefinition = "date null comment '本次逾期的结束日期（当overdueStatus为HISTORY时才有值）'")
    private LocalDate thisTimeOverdueEndDate;

    private LocalDateTime createdDate;

    @Column(length = 100)
    private String createdBy;

    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;


    /**
     * 本次逾期的状态
     */
    @Getter
    public enum OverdueStatusEnum {
        CURRENT("当前逾期"),
        HISTORY("历史逾期");

        private final String description;

        OverdueStatusEnum(String description) {
            this.description = description;
        }
    }

}
