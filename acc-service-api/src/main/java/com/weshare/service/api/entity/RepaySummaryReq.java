package com.weshare.service.api.entity;

import com.weshare.service.api.enums.AssetStatusEnum;
import com.weshare.service.api.enums.SettleTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户还款主信息,  5*
 */
@Data
@Accessors(chain=true)

public class RepaySummaryReq {

    private String projectNo;//项目编号

    private String productNo;//产品编号

    private String dueBillNo;//借据号

    private String userId;//用户ID

    private BigDecimal contractAmount;//合同金额

    private LocalDate loanDate;//放款日期

    private Integer repayDay;//每月还款日

    private Integer currentTerm;//当前期次

    private AssetStatusEnum assetStatus;

    private Integer totalTerm;//总期次

    private Integer returnTerm;//已还期数

    private LocalDate currentTermDueDate;//当前期次的应还日期

    private LocalDate currentPaidOutDate;//当前期次的结清日期

    private BigDecimal remainPrincipal;//剩余本金

    private BigDecimal remainInterest;//剩余利息

    private LocalDate settleDate;//结清日期

    private SettleTypeEnum settleType;

    private String remark;

    private LocalDate batchDate;

}
