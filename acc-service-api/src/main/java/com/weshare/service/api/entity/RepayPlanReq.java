package com.weshare.service.api.entity;

import com.weshare.service.api.enums.TermPaidOutTypeEnum;
import com.weshare.service.api.enums.TermStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class RepayPlanReq {

    private String dueBillNo;//借据号

    private Integer term;//期次

    private String projectNo;//项目编号

    private String productNo;//产品编号

    private TermStatusEnum termStatus;

    private LocalDate termStartDate;//计息开始日

    private LocalDate termDueDate;//应还款日

    private LocalDate repayDate;//本期次还清的日期

    private TermPaidOutTypeEnum termPaidOutType;

    private BigDecimal termBillAmount;//本期账单应还金额(元)

    // 应还明细项
    private BigDecimal termPrin;//应还本金(元)

    private BigDecimal termInt;//应还利息(元)

    private BigDecimal termPenalty;//应还罚息(元)

    // 实还明细项
    private BigDecimal termRepayPrin;//已还本金(元)

    private BigDecimal termRepayInt;//已还利息(元)

    private BigDecimal termRepayPenalty;//已还罚息(元)

    // 减免明细
    private BigDecimal termReduceInt;//减免利息(元)

    private LocalDate batchDate;

    private String remark;

}
