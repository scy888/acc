package com.weshare.service.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.acc.adapter.yxms.entity
 * @date: 2020-10-13 14:41:45
 * @describe:
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RebackDetailReq {

    private String dueBillNo;//借据号

    private Integer term;//期数

    private BigDecimal debitAmount;//'扣款总金额(元)

    private BigDecimal principal;//本金(元)

    private BigDecimal interest;//利息(元)

    private BigDecimal Reduceinterest;

    private String transactionResult;//交易结果 01-成功,02-失败,03处理中

    private FailReasonEnum failReason;//失败原因

    private LocalDateTime debitDate;//扣款时间

    private LocalDate batchDate;//跑批日期

    public enum FailReasonEnum {

        银行卡号错误,
        手机号错误,
        身份证号错误;

    }

}
