package com.weshare.batch.task.entity;

import lombok.Data;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(appliesTo = "task_run_log", comment = "任务日志表")
public class TaskRunLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(100) null comment '任务名称' ")
    private String taskName;

    @Column(columnDefinition = "varchar(2000) null comment '任务执行时的参数' ")
    private String taskParams;

    @Column(columnDefinition = "boolean null comment '本次任务是否执行成功' ")
    private Boolean isSuccess;

    private String remark;

    @Column(columnDefinition = "varchar(200) null comment '执行机' ")
    private String machine;

    @Column(columnDefinition = "varchar(200) null comment '线程名' ")
    private String threadName;

    @Column(columnDefinition = "datetime  null comment '开始执行时间' ")
    private LocalDateTime startTime;

    @Column(columnDefinition = "datetime null comment '执行结束时间' ")
    private LocalDateTime endTime;

    @Column(columnDefinition = "decimal(12,3) null comment '耗时(秒)' ")
    private BigDecimal totalTimeCost;

}
