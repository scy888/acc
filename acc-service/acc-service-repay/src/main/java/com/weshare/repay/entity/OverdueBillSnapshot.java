package com.weshare.repay.entity;

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

@Data
@Entity
@javax.persistence.Table(indexes = {@Index(name = "idx_overdue_bill_snapshot_due_bill_no", unique = true, columnList = "dueBillNo,batchDate")})
@Table(appliesTo = "overdue_bill_snapshot", comment = "资金端借据逾期信息按日快照表(新)")
@Accessors(chain = true)
public class OverdueBillSnapshot {

    @Id
    private Long id;

    @Column(columnDefinition = "varchar(32) null comment '项目编号'")
    private String projectNo;

    @Column(columnDefinition = "varchar(32) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "date null comment '批次日期'")
    private LocalDate batchDate;

    @Column(columnDefinition = "decimal(12,2)  null comment '当前逾期本金'")
    private BigDecimal currentOverduePrincipal; // 当前逾期本金，不区分DPD/CPD，重要

    @Column(columnDefinition = "decimal(12,2) null comment '当前逾期利息'")
    private BigDecimal currentOverdueInterest; // 当前逾期利息，不区分DPD/CPD

    @Column(columnDefinition = "decimal(12,2) null comment '当前逾期费用'")
    private BigDecimal currentOverdueFee; // 当前逾期费用，不区分DPD/CPD

    @Column(columnDefinition = "decimal(12,2) null comment '当前逾期罚息'")
    private BigDecimal currentOverduePenalty; // 当前逾期罚息，不区分DPD/CPD

    @Column(columnDefinition = "date null comment '当前逾期起始日期（DPD）'")
    private LocalDate dpdCurrentOverdueStartDate; // 当前逾期起始日期，DPD口径

    @Column(columnDefinition = "int null comment '当前逾期天数（DPD）'")
    private Integer dpdCurrentOverdueDays; // 当前逾期天数，DPD口径

    @Column(columnDefinition = "int null comment '第几次逾期'")
    private Integer overdueOrder;

    private LocalDateTime createdDate;

    @Column(length = 100)
    private String createdBy;


}
