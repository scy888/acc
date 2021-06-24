package com.weshare.repay.entity;

import com.weshare.service.api.enums.AssetStatusEnum;
import com.weshare.service.api.enums.SettleTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户还款主信息,  5*
 */
@Data
@Accessors(chain = true)
public class RepaySummaryBack {

    private String id;
    private String projectNo;
    private String productNo;
    private String dueBillNo;
    private String userId;
    private BigDecimal contractAmount;
    private LocalDate loanDate;
    private Integer repayDay;
    private Integer currentTerm;
    private AssetStatusEnum assetStatus;
    private Integer totalTerm;
    private Integer returnTerm;
    private LocalDate currentTermDueDate;
    private LocalDate currentPaidOutDate;
    private BigDecimal remainPrincipal;
    private BigDecimal remainInterest;
    private LocalDate settleDate;
    private SettleTypeEnum settleType;
    private String remark;
    private LocalDate batchDate;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
}
