package com.weshare.service.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RepaymentPlanReq {

    private String dueBillNo;//借据号

    private Integer term;//期次

    private LocalDate repaymentDate;//应还日

    private BigDecimal shouldMonthMoney;//应还月供(元)

    private BigDecimal shouldCapitalMoney;//应还本金(元)

    private BigDecimal shouldInterestlMoney;//应还利息(元)

    private LocalDate batchDate;//跑批日期

}
