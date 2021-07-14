package com.weshare.adapter.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.entity
 * @date: 2021-07-14 18:57:29
 * @describe:
 */
@Entity
@Data
public class InterfaceReqLog {

    @Id
    private String id;

    private String serviceId;

    @Column(columnDefinition = "text not null comment '请求数据' ")
    private String originalReqMsg;

    private LocalDate batchDate;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    public class originalReqMsg {
        @JsonProperty(value = "service", index = 1)
        private String service;
        @JsonProperty(value = "content", index = 2)
        private String content;
        @JsonProperty(value = "product_ame", index = 3)
        private String productName;
        @JsonProperty(value = "project_no", index = 4)
        private String projectNo;
        @JsonProperty(value = "create_date", index = 5)
        @JsonFormat(pattern = "yyyyMMddHHmmss")
        private LocalDateTime createDate;

        public class LoanDetail {

            private String dueBillNo;//借据号

            private LocalDate loanDate;//放款日期

            private BigDecimal loanAmount;//放款金额

            private String serialNum;//放款流水号

            private Integer term;//期数

            private String AccountNum;//放款账号

            private String loanStatus;//放款状态 01-成功,02-失败

            private LocalDate batchDate;
        }
    }
}
