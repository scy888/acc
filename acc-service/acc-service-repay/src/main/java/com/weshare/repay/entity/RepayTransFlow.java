package com.weshare.repay.entity;

import com.weshare.service.api.enums.TransFlowTypeEnum;
import com.weshare.service.api.enums.TransStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户银行账户交易流水，4*
 */
@Data
@Accessors(chain = true)
@Entity
@javax.persistence.Table(indexes = {
        @Index(name = "idx_repay_trans_flow_due_bill_no", columnList = "dueBillNo"),
        @Index(name = "idx_repay_trans_flow_batch_date", columnList = "batchDate"),
        @Index(name = "idx_repay_trans_flow_project_no", columnList = "projectNo")
})
@Table(appliesTo = "repay_trans_flow", comment = "用户银行账户交易流水")
public class RepayTransFlow {

    @Id
    private String id;

    @Column(columnDefinition = "varchar(20) null comment '项目编号'")
    private String projectNo;

    @Column(columnDefinition = "varchar(20) null comment '产品编号'")
    private String productNo;

    @Column(columnDefinition = "varchar(50) not null comment '流水号' ")
    private String flowSn;

    @Column(columnDefinition = "varchar(50) not null comment '借据单号' ")
    private String dueBillNo;

    @Column(columnDefinition = "varchar(20) not null comment '交易类型 TransFlowTypeEnum' ")
    @Enumerated(EnumType.STRING)
    private TransFlowTypeEnum transFlowType;

    @Column(columnDefinition = "decimal(12,2) not null comment '金额' ")
    private BigDecimal transAmount;

    @Column(columnDefinition = "datetime null comment '交易时间' ")
    private LocalDateTime transTime;

    @Column(columnDefinition = "varchar(50) not null comment '交易状态' ")
    @Enumerated(EnumType.STRING)
    private TransStatusEnum transStatus;

    @Column(columnDefinition = "varchar(100) comment '交易备注' ")
    private String remark;

    @Column(columnDefinition = "date not null comment '批量时间' ")
    private LocalDate batchDate;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

}
