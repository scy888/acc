package com.weshare.service.api.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.entity
 * @date: 2021-05-29 11:44:23
 * @describe:
 */
@Data
@Accessors(chain = true)
public class RefundTicketReq {

    private String dueBillNo;//借据号

    private BigDecimal loanAmount;//放款金额

    private LocalDate loanDate;//放款日期

    private String refundStatus;//退票状态

    private String AccountNum;//放款账号

    private LocalDateTime refundDate;//退款时间

    private LocalDate batchDate;//跑批时间
}
