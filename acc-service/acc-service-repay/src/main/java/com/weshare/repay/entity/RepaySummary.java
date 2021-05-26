package com.weshare.repay.entity;

import com.weshare.service.api.enums.AssetStatusEnum;
import com.weshare.service.api.enums.SettleTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户还款主信息,  5*
 */
@Data
@Accessors(chain=true)
@Entity
@javax.persistence.Table(indexes = {
        @Index(name = "idx_repay_summary_due_bill_no", columnList = "dueBillNo", unique = true),
        @Index(name = "idx_repay_summary_batch_date", columnList = "batchDate"),
        @Index(name = "idx_repay_summary_repay_day", columnList = "repayDay"),
        @Index(name = "idx_repay_summary_project_no", columnList = "projectNo")
})
@Table(appliesTo = "repay_summary", comment = "用户还款主信息表")
public class RepaySummary {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(20) null comment '项目编号'")
    private String projectNo;

    @Column(columnDefinition = "varchar(20) null comment '产品编号'")
    private String productNo;

    @Column(columnDefinition = "varchar(32) not null comment '借据号' ")
    private String dueBillNo;

    @Column(columnDefinition = "varchar(32) null comment '用户ID' ")
    private String userId;

    @Column(columnDefinition = "decimal(12, 2) not null comment '合同金额' ")
    private BigDecimal contractAmount;

    @Column(columnDefinition = "date null comment '放款日期' ")
    private LocalDate loanDate;

    @Column(columnDefinition = "int null comment '每月还款日' ")
    private Integer repayDay;

    @Column(columnDefinition = "int null comment '当前期次' ")
    private Integer currentTerm;

    @Column(columnDefinition = "varchar(20) not null comment '资产状态 AssetStatusEnum'")
    @Enumerated(EnumType.STRING)
    private AssetStatusEnum assetStatus;

    @Column(columnDefinition = "int(3) null comment '总期次' ")
    private Integer totalTerm;

    @Column(columnDefinition = "int(3) null comment '已还期数' ")
    private Integer returnTerm;

    @Column(columnDefinition = "date null comment '当前期次的应还日期' ")
    private LocalDate currentTermDueDate;

    @Column(columnDefinition = "date null comment '当前期次的结清日期' ")
    private LocalDate currentPaidOutDate;

    @Column(columnDefinition = "decimal(12, 2) null comment '剩余本金' ")
    private BigDecimal remainPrincipal;

    @Column(columnDefinition = "decimal(12, 2) null comment '剩余利息' ")
    private BigDecimal remainInterest;

    @Column(columnDefinition = "date null comment '结清日期'")
    private LocalDate settleDate;

    @Column(columnDefinition = "varchar(20) null comment '结清原因类型 SettleTypeEnum'")
    @Enumerated(EnumType.STRING)
    private SettleTypeEnum settleType;

    @Column(columnDefinition = "varchar(50) null comment '备注' ")
    private String remark;

    @Column(columnDefinition = "date null comment '批次日期' ")
    private LocalDate batchDate;

    private LocalDateTime createdDate;

    @Column(length = 100)
    private String createdBy;

    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;
}
