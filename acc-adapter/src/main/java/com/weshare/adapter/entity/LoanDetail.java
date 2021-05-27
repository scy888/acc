package com.weshare.adapter.entity;


import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(indexes = {
        @Index(name = "loan_detail_due_bill_no", unique = false, columnList = "dueBillNo")
})
@org.hibernate.annotations.Table(appliesTo = "loan_detail", comment = "放款明细表")
public class LoanDetail {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(32) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "date not null comment '放款日期'")
    private LocalDate loanDate;

    @Column(columnDefinition = "decimal(12,2) not null comment '放款金额'")
    private BigDecimal loanAmount;

    @Column(columnDefinition = "varchar(30) null comment '放款流水号'")
    private String serialNum;

    @Column(columnDefinition = "int not null comment '期数'")
    private int term;

    @Column(columnDefinition = "varchar(40) null comment '放款账号'")
    private String AccountNum;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(4) not null comment '放款状态 01-成功,02-失败'")
    private LoanStatusEnum loanStatus;

    @Column(columnDefinition = "date null comment '跑批日期'")
    private LocalDate batchDate;

    @Column(columnDefinition = "datetime null comment '创建时间'")
    private LocalDateTime createdDate;

    @Column(columnDefinition = "datetime null comment '修改时间'")
    private LocalDateTime lastModifiedDate;

    @Getter
    public enum LoanStatusEnum {

        成功("01"),
        失败("02");

        private final String desc;

        LoanStatusEnum(String desc) {
            this.desc = desc;
        }
    }
}
