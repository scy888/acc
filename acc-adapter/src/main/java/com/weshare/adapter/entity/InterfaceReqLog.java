package com.weshare.adapter.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.entity
 * @date: 2021-07-14 18:57:29
 * @describe:
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceReqLog {

    @Id
    private String id;

    private String serviceId;

    @Column(columnDefinition = "text not null comment '请求数据' ")
    private String originalReqMsg;

    private LocalDate batchDate;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OriginalReqMsg {
        @JsonProperty(value = "service")
        private String service;
        @JsonProperty(value = "content")
        private String content;
        @JsonProperty(value = "product_ame")
        private String productName;
        @JsonProperty(value = "project_no")
        private String projectNo;
        @JsonProperty("batch_date")
        @JsonFormat(pattern = "yyyyMMdd")
        private LocalDate batchDate;
        @JsonProperty(value = "create_date")
        @JsonFormat(pattern = "yyyyMMddHHmmss")
        private LocalDateTime createDate;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class LoanDetail {

            @JsonProperty(value = "due_bill_no")
            private String dueBillNo;//借据号
            @JsonProperty(value = "loan_date")
            private LocalDate loanDate;//放款日期
            @JsonProperty(value = "loan_amount")
            private BigDecimal loanAmount;//放款金额
            @JsonProperty(value = "serial_num")
            private String serialNum;//放款流水号
            @JsonProperty(value = "term")
            private Integer term;//期数
            @JsonProperty(value = "account_num")
            private String accountNum;//放款账号
            @JsonProperty(value = "loan_status")
            private String loanStatus;//放款状态 01-成功,02-失败
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class RepayPlan {

            @JsonProperty(value = "due_bill_no")
            private String dueBillNo;
            @JsonProperty(value = "total_term")
            private Integer totalTerm;
            @JsonProperty(value = "due_bill_no_list")
            private List<DueBillNoList> dueBillNoList;

            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class DueBillNoList {

                @JsonProperty(value = "term")
                private Integer term;
                @JsonProperty(value = "repayment_date")
                private LocalDate repaymentDate;//应还日
                @JsonProperty(value = "should_month_money")
                private BigDecimal shouldMonthMoney;//应还月供(元)
                @JsonProperty(value = "should_capital_money")
                private BigDecimal shouldCapitalMoney;//应还本金(元)
                @JsonProperty(value = "should_interest_money")
                private BigDecimal shouldInterestMoney;//应还利息(元)
            }
        }
    }

    public enum ServiceEnum {
        LOAN_DETAIL,
        REPAY_PLAN;
    }
}
