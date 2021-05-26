package com.weshare.service.api.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 借款合同表 5*
 *
 * @author jiancai.zhou
 * @date 2020-07-14 14:17
 **/
@Data
public class LoanContractReq {


    private String dueBillNo;//借据号

    private String userId;//用户编号

    private String projectNo;//项目编号

    private String productNo;//产品编号

    private String productName;//产品名称

    private BigDecimal contractAmount;//贷款金额

    private BigDecimal interestRate;//贷款利率

    private Integer totalTerm;//贷款总期数

    private Integer repayDay;//每月还款日

    private LocalDate firstTermDueDate;//第一期应还日期

    private LocalDate lastTermDueDate;//最后一期应还日期

    private BigDecimal principal;//本金(元)

    private BigDecimal interest;//利息(元)

    private LocalDate batchDate;//

    private String remark;

}
