package com.weshare.adapter.entity;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(indexes = {
        @Index(name = "repayment_plan_due_bill_no_and_term_batch_date", columnList = "dueBillNo,term,batchDate")
})
@org.hibernate.annotations.Table(appliesTo = "repayment_detail", comment = "还款明细表")
public class RepaymentDetail {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(32) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "varchar(2) not null comment '扣款类型 01-正常扣款,02-提前结清扣款'")
    @Enumerated(EnumType.STRING)
    private DebitTypeEnum debitType;

    @Column(columnDefinition = "datetime not null comment '交易时间'")
    private LocalDateTime tradeDate;

    @Column(columnDefinition = "int not null comment '期数'")
    private Integer term;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '还款总金额(元)'")
    private BigDecimal repaymentAmount;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '本金(元)'")
    private BigDecimal principal;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '利息(元)'")
    private BigDecimal interest;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '罚息(元)'")
    private BigDecimal Penalty;

    @Column(columnDefinition = "date null comment '跑批日期'")
    private LocalDate batchDate;

    @Column(columnDefinition = "datetime null comment '创建时间'")
    private LocalDateTime createdDate;

    @Column(columnDefinition = "datetime null comment '修改时间'")
    private LocalDateTime lastModifiedDate;

    @Getter
    public enum DebitTypeEnum {
       正常扣款("01"),
       提前结清扣款("02");

       private String code;

        DebitTypeEnum(String code) {
            this.code = code;
        }
    }
}
