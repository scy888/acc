package com.weshare.batch.Listener;

import com.weshare.batch.controller.BatchController;
import com.weshare.batch.entity.StartEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.Listener
 * @date: 2021-06-15 19:14:16
 * @describe:
 */
@Component
@Slf4j
public class JobListener implements JobExecutionListener {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        String jobName = jobExecution.getJobInstance().getJobName();
        BatchStatus status = jobExecution.getStatus();
        log.info("当前执行的job名称:{},状态:{}", jobName, status);

        JobParameters jobParameters = jobExecution.getJobParameters();
        //发布事件广播
        applicationEventPublisher.publishEvent(
                new StartEvent()
                        .setJobName(jobName)
                        .setBatchDate(jobParameters.getString("batchDate"))
                        .setEndDate(jobParameters.getString("endDate"))
                        .setProjectNo(jobParameters.getString("projectNo"))
                        .setRemark(jobParameters.getString("remark"))
                        .setStatus(status.name())
        );
    }

    //监听广播
    @EventListener
    public void ListenPublishEvent(StartEvent startEvent) {

        String jobName = startEvent.getJobName();
        String batchDate = startEvent.getBatchDate();
        String endDate = startEvent.getEndDate();
        String projectNo = startEvent.getProjectNo();
        String remark = startEvent.getRemark();
        String status = startEvent.getStatus();

        String msg = String.format("监听到广播的信息如下:\n jobName:%s\n batchDate:%s\n endDate:%s\n projectNo:%s\n remark:%s\n status:%s".replaceAll(" ", ""),
                jobName, batchDate, endDate, projectNo, remark, status);
        log.info(msg);

        if (!LocalDate.parse(batchDate).isBefore(LocalDate.parse(endDate))) {
            log.info("开始跑批时间不在结束跑批时间之前,无需自动跑批...");
            return;
        }

        if (BatchStatus.COMPLETED == BatchStatus.valueOf(status)) {
            LocalDate startBatch;
            switch (batchDate) {
                case "2020-05-15":
                    startBatch = LocalDate.parse(batchDate).withDayOfMonth(30);
                    break;
                case "2020-05-30":
                    startBatch = LocalDate.parse(batchDate).with(ChronoField.MONTH_OF_YEAR, 6).with(ChronoField.DAY_OF_MONTH, 15);
                    break;
                case "2020-06-15":
                    startBatch = LocalDate.parse(batchDate).with(ChronoField.MONTH_OF_YEAR, 7).with(ChronoField.DAY_OF_MONTH, 20);
                    break;
                case "2020-07-20":
                    startBatch = LocalDate.parse(batchDate).with(ChronoField.MONTH_OF_YEAR, 8).with(ChronoField.DAY_OF_MONTH, 20);
                    break;
                case "2020-08-20":
                    startBatch = LocalDate.parse(batchDate).with(ChronoField.MONTH_OF_YEAR, 10).with(ChronoField.DAY_OF_MONTH, 15);
                    break;
                default:
                    throw new RuntimeException("请选择正确的日期...");
            }

            try {
                log.info("开始跑:{}日期的批...", startBatch);
                Thread.sleep(3 * 1000);
                applicationContext.getBean(BatchController.class).startJob(jobName, startBatch.toString(), endDate, projectNo, remark);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
