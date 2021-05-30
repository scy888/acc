package com.weshare.adapter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.entity
 * @date: 2021-05-29 11:13:15
 * @describe:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(indexes = {@Index(name = "refund_ticket_du_bill_no_refund_status", unique = true, columnList = "dueBillNo,refundStatus")})
@org.hibernate.annotations.Table(appliesTo = "refund_ticket", comment = "退票文件")
public class RefundTicket {
    @Id
    private String id;

    @Column(columnDefinition = "varchar(20) not null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "decimal(12,2) not null comment '放款金额'")
    private BigDecimal loanAmount;

    @Column(columnDefinition = "date not null comment '放款日期'")
    private LocalDate loanDate;

    @Column(columnDefinition = "varchar(10) not null comment '退票状态'")
    @Enumerated(EnumType.STRING)
    private RefundStatusEnum refundStatus;

    @Column(columnDefinition = "varchar(40) null comment '放款账号'")
    private String AccountNum;

    @Column(columnDefinition = "datetime null comment '退款时间'")
    private LocalDateTime refundDate;

    @Column(columnDefinition = "datetime null comment '跑批时间'")
    private LocalDate batchDate;

    @Column(columnDefinition = "datetime null comment '创建时间'")
    private LocalDateTime createDate;

    @Column(columnDefinition = "datetime null comment '修改时间'")
    private LocalDateTime lastModifiedDate;

    @Getter
    public enum RefundStatusEnum {

        退票成功("01"),
        退票失败("02");

        private String code;

        RefundStatusEnum(String code) {
            this.code = code;
        }
    }
}
