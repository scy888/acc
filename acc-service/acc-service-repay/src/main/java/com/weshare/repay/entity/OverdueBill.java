package com.weshare.repay.entity;

import com.weshare.service.api.enums.AssetStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@javax.persistence.Table(indexes = {@Index(name = "idx_overdue_bill_due_bill_no", unique = true, columnList = "dueBillNo")})
@Table(appliesTo = "overdue_bill", comment = "资金端借据逾期信息(新)")
@Accessors(chain = true)
public class OverdueBill {

    @Id
    private Long id;

    @Column(columnDefinition = "varchar(32) null comment '项目编号'")
    private String projectNo;

    @Column(columnDefinition = "varchar(32)  null comment '产品编号'")
    private String productNo;

    @Column(columnDefinition = "varchar(32) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "date null comment '批次日期'")
    private LocalDate batchDate;

    @Column(columnDefinition = "varchar(25) null comment '资产状态'")
    @Enumerated(EnumType.STRING)
    private AssetStatusEnum assetStatus;

    @Column(columnDefinition = "int null comment '当前逾期期次'")
    private Integer currentOverdueTerm; // 当前逾期期次，不区分DPD/CDP

    @Column(columnDefinition = "int null comment '当前逾期期数'")
    private Integer currentOverdueTermsCount; // 当前逾期期数，不区分DPD/CPD

    @Column(columnDefinition = "varchar(100) null comment '当前逾期的所有期次'")
    private String currentOverdueTerms; // 当前逾期的期次（多期逗号相连），不区分DPD/CPD，当前逾期的所有期次，例： 2,3,4

    @Column(columnDefinition = "decimal(12,2)  null comment '当前逾期本金'")
    private BigDecimal currentOverduePrincipal; // 当前逾期本金，不区分DPD/CPD

    @Column(columnDefinition = "decimal(12,2) null comment '当前逾期利息'")
    private BigDecimal currentOverdueInterest; // 当前逾期利息，不区分DPD/CPD

    @Column(columnDefinition = "decimal(12,2) null comment '当前逾期费用'")
    private BigDecimal currentOverdueFee; // 当前逾期费用，不区分DPD/CPD

    @Column(columnDefinition = "decimal(12,2) null comment '当前逾期罚息'")
    private BigDecimal currentOverduePenalty; // 当前逾期罚息，不区分DPD/CPD

    @Column(columnDefinition = "date null comment '当前逾期起始日期'")
    private LocalDate dpdCurrentOverdueStartDate; // 当前逾期起始日期，DPD口径

    @Column(columnDefinition = "int null comment '当前逾期天数'")
    private Integer dpdCurrentOverdueDays; // 当前逾期天数，DPD口径

    @Column(columnDefinition = "int null comment '历史最大逾期天数'")
    private Integer dpdHisMaxOverdueDays; // DPD口径，历史最大逾期天数

    @Column(columnDefinition = "int null comment '累计逾期期数'")
    private Integer hisTotalOverdueTermsCount; // 累计逾期期数，不区分dpd/cpd。包含历史曾逾期过的期次。 = sum(逾期已还的期数 + 当前期次状态为逾期的期数)

    @Column(columnDefinition = "int null comment '历史单次最长逾期期数(DPD)'")
    private Integer dpdHisMaxOverdueTermsCount; // 历史单次最长逾期期数，区分DPD/CPD

    @Column(columnDefinition = "date null comment '历史上第一次逾期的日期'")
    private LocalDate hisFirstOverdueDate; // 首逾日期，历史上第一次逾期的日期，不区分DPD/CDP

    @Column(columnDefinition = "decimal(12,2) null comment '累计逾期本金'")
    private BigDecimal hisTotalOverduePrincipal; // 累计逾期本金 = =SUM(逾期已还的应还本金) + SUM(逾期未还的应还本金 - 已还本金)

    @Column(columnDefinition = "decimal(12,2) null comment '历史最大逾期本金(DPD)'")
    private BigDecimal dpdHisMaxOverduePrincipal; // 历史最大逾期本金，区分dpd/cpd

    @Column(columnDefinition = "int null comment '累计逾期天数'")
    private Integer hisTotalOverdueDays; // 累计逾期天数（含历史逾期已还的期次），不区分DPD/CPD
    // 不区分dpd、cpd。sum(历史逾期期次的已还日 - 历史逾期期次的应还日) + 当前逾期天数

    private LocalDateTime createdDate;

    @Column(length = 100)
    private String createdBy;

    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;


}
