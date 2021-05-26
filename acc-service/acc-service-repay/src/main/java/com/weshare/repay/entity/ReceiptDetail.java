package com.weshare.repay.entity;

import com.weshare.service.api.enums.FeeTypeEnum;
import com.weshare.service.api.enums.ReceiptTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 还款实收收据明细，4*
 */
@Data
@Accessors(chain = true)
@Entity
@Table(appliesTo = "receipt_detail", comment = "还款实收收据明细")
@javax.persistence.Table(indexes = {
        @Index(name = "idx_receipt_detail_due_bill_no", columnList = "dueBillNo"),
        @Index(name = "idx_receipt_detail_batch_date", columnList = "batchDate"),
        @Index(name = "idx_receipt_detail_project_no", columnList = "projectNo")
})
public class ReceiptDetail {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(20) null comment '项目编号'")
    private String projectNo;

    @Column(columnDefinition = "varchar(20) null comment '产品编号'")
    private String productNo;

    @Column(columnDefinition = "varchar(50) not null comment '借据单号' ")
    private String dueBillNo;

    @Column(columnDefinition = "int(3) null comment '总期次' ")
    private Integer totalTerm;

    @Column(columnDefinition = "int(3) not null comment '期次' ")
    private Integer term;

    @Column(columnDefinition = "decimal(12,2) not null comment '实收金额' ")
    private BigDecimal amount;

    @Column(columnDefinition = "varchar(20) not null comment '费用类型 FeeTypeEnum' ")
    @Enumerated(EnumType.STRING)
    private FeeTypeEnum feeType;

    @Column(columnDefinition = "varchar(20) not null comment '实收类型 ReceiptTypeEnum' ")
    @Enumerated(EnumType.STRING)
    private ReceiptTypeEnum receiptType;

    @Column(columnDefinition = "varchar(50) null comment '关联的还款流水号' ")
    private String flowSn;

    @Column(columnDefinition = "date null comment '还款日期' ")
    private LocalDate repayDate;

    @Column(columnDefinition = "varchar(50) null comment '备注' ")
    private String remark;

    @Column(columnDefinition = "date null comment '批次日期' ")
    private LocalDate batchDate;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

}
