package com.weshare.batch.task.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
@Table(appliesTo = "task_config", comment = "任务配置表")
@Accessors(chain = true)
public class TaskConfig {

    @Id
    private String taskName;

    @Column(columnDefinition = "varchar(100) null comment '任务描述' ")
    private String description;

    @Column(columnDefinition = "varchar(100) not null comment 'cron表达式（6位），例：0 0/2 * * * ?' ")
    private String cron;

    @Column(columnDefinition = "boolean not null comment '是否启用' ")
    private Boolean isEnabled;

    @Column(columnDefinition = "boolean not null comment '是否正在运行' ")
    private Boolean isRunning;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String lastModifiedBy;

}
