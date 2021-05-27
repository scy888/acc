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
public class LoanDetailReq {

    private String dueBillNo;//借据号

    private LocalDate loanDate;//放款日期

    private BigDecimal loanAmount;//放款金额

    private String serialNum;//放款流水号

    private Integer term;//期数

    private String AccountNum;//放款账号

    private String loanStatus;//放款状态 01-成功,02-失败

    private LocalDate batchDate;

}
