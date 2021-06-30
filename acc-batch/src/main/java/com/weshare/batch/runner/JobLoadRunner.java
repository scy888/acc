package com.weshare.batch.runner;

import com.weshare.batch.controller.BatchController;
import com.weshare.batch.enums.BatchJobEnum;
import com.weshare.batch.task.TaskListScheduler;
import com.weshare.batch.task.entity.BatchJobControl;
import com.weshare.batch.task.entity.TaskConfig;
import com.weshare.batch.task.instance.YxmsTask;
import com.weshare.batch.task.repo.BatchJobControlRepo;
import com.weshare.batch.task.repo.TaskConfigRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.runner
 * @date: 2021-05-14 16:11:42
 * @describe:
 */
@Component
@Slf4j
public class JobLoadRunner implements ApplicationRunner {
    @Autowired
    private JobRegistry jobRegistry;
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private BatchController batchController;
    @Autowired
    private TaskListScheduler taskListScheduler;
    @Autowired
    private BatchJobControlRepo batchJobControlRepo;
    @Autowired
    private TaskConfigRepo taskConfigRepo;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("job注册表中的job有:{}", jobRegistry.getJobNames());
        log.info("job浏览器中的job有:{}", jobExplorer.getJobNames());
        //batchController.test();
        //taskListScheduler.initTask();

        //初始化job任务表
        for (String jobName : jobRegistry.getJobNames()) {
            if (jobRegistry.getJob(BatchJobEnum.yxmsJob.name()).getName().equals(jobName)) {
                batchJobControlRepo.findById(jobName).ifPresentOrElse(e ->
                        log.info("该jobName:{},在batch_job_control表中存在无需初始化...", jobName), () ->
                        batchJobControlRepo.save(
                                new BatchJobControl()
                                        .setJobName(jobName)
                                        .setIsRunning(false)
                                        .setCreateDate(LocalDateTime.now())
                                        .setLastModifyDate(LocalDateTime.now())
                        ));
                break;
            }
        }
        //初始化定时任务
        taskConfigRepo.findById(new YxmsTask().getTaskName()).ifPresentOrElse(e -> {

            threadPoolTaskScheduler.schedule((Runnable)applicationContext.getBean(new YxmsTask().getTaskName()), new CronTrigger(e.getCron()));
            log.info("定时任务已初始化,无需初始化...");
        }, () -> {
            TaskConfig taskConfig = new TaskConfig();
            taskConfig.setTaskName(new YxmsTask().getTaskName());
            taskConfig.setDescription("易鑫民生跑批任务");
            taskConfig.setCron("0 0/1 * * * ?");
            taskConfig.setIsEnabled(true);
            taskConfig.setIsRunning(false);
            taskConfig.setCreatedDate(LocalDateTime.now());
            taskConfigRepo.save(taskConfig);
            //threadPoolTaskScheduler.schedule((Runnable)applicationContext.getBean(new YxmsTask().getTaskName()), new CronTrigger(taskConfig.getCron()));
        });
    }
}
