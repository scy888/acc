package com.weshare.service.api.entity;

import com.weshare.service.api.enums.FeeTypeEnum;
import com.weshare.service.api.enums.ReceiptTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 还款实收收据明细，4*
 */
@Data
@Accessors(chain = true)
public class ReceiptDetailReq {

    private String projectNo;//项目编号

    private String productNo;//产品编号

    private String dueBillNo;//借据单号

    private Integer totalTerm;//总期次

    private Integer term;//期次

    private BigDecimal amount;//实收金额

    private FeeTypeEnum feeType;

    private ReceiptTypeEnum receiptType;

    private String flowSn;//关联的还款流水号

    private LocalDate repayDate;//还款日期

    private String remark;

    private LocalDate batchDate;

}
