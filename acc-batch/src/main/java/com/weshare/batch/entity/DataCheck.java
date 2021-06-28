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

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@Table(appliesTo = "data_check", comment = "数据校验记录表")
@javax.persistence.Table(indexes = {
        @Index(name = "idx_project_no_batch_date", unique = false, columnList = "projectNo, batchDate")
})
public class DataCheck {
    @Id
    @Column(columnDefinition = "varchar(50) not null comment '主键'")
    private String id;

    @Column(columnDefinition = "varchar(20) not null comment '项目编号' ")
    private String projectNo;

    @Column(columnDefinition = "date not null comment '批次日期' ")
    private LocalDate batchDate;

    @Column(columnDefinition = "varchar(100) null comment '要校验的分类编号'")
    private String checkName;

    @Column(columnDefinition = "varchar(500) null comment '校验结果描叙'")
    private String checkDesc;

    @Column(columnDefinition = "boolean null comment '批次校验结果' ")
    private Boolean checkResult;

    @Column(columnDefinition = "int null comment '不通过数量' ")
    private Integer errorCount;

    @Column(columnDefinition = "int null comment '校验耗时(秒)' ")
    private Integer costSecond;

    @Column(columnDefinition = "varchar(500) null comment '备注'")
    private String remark;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
