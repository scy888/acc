package com.weshare.service.api.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 放款交易流水，4*
 */
@Data
@Accessors(chain = true)
public class LoanTransFlowReq {

    private String flowSn;//流水号

    private String projectNo;//项目编号

    private String productNo;//产品编号

    private String dueBillNo;//借据单号

    private BigDecimal transAmount;//金额

    private String bankAccountName;//银行账户名称

    private String bankAccountNo;//银行卡号

    private LocalDateTime transTime;//交易时间

    private String remark;//交易备注

    private LocalDate batchDate;
}
