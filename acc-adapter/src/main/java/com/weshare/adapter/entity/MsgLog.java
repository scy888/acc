package com.weshare.adapter.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {
        @Index(name = "msg_log_msg_type", unique = false, columnList = "msgType"),
        @Index(name = "msg_log_apply_no", unique = false, columnList = "applyNo")
})
@org.hibernate.annotations.Table(appliesTo = "msg_log", comment = "adapter请求日志")
public class MsgLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(32) null comment '项目编号' ")
    private String projectNo;

    @Column(columnDefinition = "varchar(32) null comment '产品编号' ")
    private String productNo;

    @Column(columnDefinition = "varchar(50) not null comment '报文类型' ")
    @Enumerated(EnumType.STRING)
    private MsgTypeEnum msgType;

    @Column(columnDefinition = "varchar(200) null comment '请求url' ")
    private String url;

    @Column(columnDefinition = "varchar(50) null comment '申请单号' ")
    private String applyNo;

    @Column(columnDefinition = "text null comment '请求数据' ")
    private String reqData;

    @Column(columnDefinition = "text null comment '响应数据' ")
    private String resData;

    @Column(columnDefinition = "varchar(50) null comment '请求接口响应时间' ")
    private String requestRiskTime;

    @Version
    private Integer version;

    private LocalDate batchDate;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column(columnDefinition = "varchar(64) null comment '原始数据ID' ")
    private String originalDataLogId;

    public enum MsgTypeEnum {
        LOAN_DETAIL,
        REPAYMENT_PLAN,
        REFUND_TICKET,
    }
}

