package com.weshare.batch.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author v_tianwenkai
 * @Description
 * @Date 2020/11/10 15:00
 */
@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@Table(appliesTo = "data_check_detail", comment = "数据校验记录详情表")
@javax.persistence.Table(indexes = {
        @Index(name = "idx_due_bill_no", unique = false, columnList = "dueBillNo"),
        @Index(name = "idx_batch_date", unique = false, columnList = "batchDate"),
})
public class DataCheckDetail {
    @Id
    @Column(columnDefinition = "varchar(50) not null comment '主键'")
    private String id;

    @Column(columnDefinition = "varchar(20) not null comment '项目编号' ")
    private String projectNo;

    @Column(columnDefinition = "varchar(100) null comment '借据号'")
    private String dueBillNo;

    @Column(columnDefinition = "date not null comment '批次日期' ")
    private LocalDate batchDate;

    @Column(columnDefinition = "varchar(500) null comment '校验类型'")
    private String checkType;


    @Column(columnDefinition = "varchar(1000) null comment '描述'")
    private String description;


    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
