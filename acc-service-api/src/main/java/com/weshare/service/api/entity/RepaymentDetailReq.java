package com.weshare.service.api.entity;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RepaymentDetailReq {

    private String dueBillNo;//借据号

    private String debitType;//扣款类型 01-正常扣款,02-提前结清扣款

    private LocalDateTime tradeDate;//交易时间

    private Integer term;//期数

    private BigDecimal repaymentAmount;//还款总金额(元)

    private BigDecimal principal;//本金(元)

    private BigDecimal interest;//利息(元)

    private BigDecimal Penalty;//罚息(元)

    private LocalDate batchDate;

}
