package com.weshare.adapter.entity;

import com.weshare.service.api.entity.RebackDetailReq;
import com.weshare.service.api.enums.TransFlowTypeEnum;
import lombok.AllArgsConstructor;
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

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.acc.adapter.yxms.entity
 * @date: 2020-10-13 14:41:45
 * @describe:
 */
@Data
@Accessors(chain = true)
@Entity
@Table(indexes = {
        @Index(name = "reback_detail_due_bill_no_term_batch_date", columnList = "dueBillNo,term,batchDate")
})
@org.hibernate.annotations.Table(appliesTo = "reback_detail", comment = "扣款明细表")
public class RebackDetail {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(32) not null comment '订单号'")
    private String dueBillNo;

    @Column(columnDefinition = "int not null comment '期数'")
    private Integer term;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '扣款总金额(元)'")
    private BigDecimal debitAmount;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '本金(元)'")
    private BigDecimal principal;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '利息(元)'")
    private BigDecimal interest;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '减免利息(元)'")
    private BigDecimal reduceInterest;

    @Column(columnDefinition = "decimal(12,2) default 0.00 comment '罚息(元)'")
    private BigDecimal Penalty;

    @Column(columnDefinition = "varchar(2) not null comment '交易结果 01-成功,02-失败,03处理中'")
    @Enumerated(EnumType.STRING)
    private RebackDetailReq.TransactionResult transactionResult;

    @Column(columnDefinition = "varchar(25) not null comment '交易类型'")
    @Enumerated(EnumType.STRING)
    private TransFlowTypeEnum transFlowType;

    @Column(columnDefinition = "varchar(100) null comment '失败原因'")
    @Enumerated(EnumType.STRING)
    private RebackDetailReq.FailReasonEnum failReason;

    @Column(columnDefinition = "datetime null comment '扣款时间'")
    private LocalDateTime debitDate;

    @Column(columnDefinition = "date null comment '跑批日期'")
    private LocalDate batchDate;

    @Column(columnDefinition = "datetime null comment '创建时间'")
    private LocalDateTime createdDate;

    @Column(columnDefinition = "datetime null comment '修改时间'")
    private LocalDateTime lastModifiedDate;

}
