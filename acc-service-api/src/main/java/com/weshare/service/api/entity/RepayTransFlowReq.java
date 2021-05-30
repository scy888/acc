package com.weshare.service.api.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户银行账户交易流水，4*
 */
@Data
@Accessors(chain = true)
public class RepayTransFlowReq {

    private String projectNo;//项目编号

    private String productNo;//产品编号

    private String flowSn;//流水号

    private String dueBillNo;//借据单号

    private String transFlowType;//交易类型

    private BigDecimal transAmount;//金额

    private LocalDateTime transTime;//交易时间

    private String transStatus;//交易状态

    private String remark;//交易备注

    private LocalDate batchDate;//批量时间

}
