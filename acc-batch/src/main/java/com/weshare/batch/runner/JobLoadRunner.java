package com.weshare.batch.runner;

import com.weshare.batch.controller.BatchController;
import com.weshare.batch.enums.BatchJobEnum;
import com.weshare.batch.task.TaskListScheduler;
import com.weshare.batch.task.entity.BatchJobControl;
import com.weshare.batch.task.entity.TaskConfig;
import com.weshare.batch.task.instance.HtmsTask;
import com.weshare.batch.task.instance.YxmsTask;
import com.weshare.batch.task.repo.BatchJobControlRepo;
import com.weshare.batch.task.repo.TaskConfigRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.runner
 * @date: 2021-05-14 16:11:42
 * @describe:
 */
@RestController
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
    @Value("${spring.data.code:盛重阳}")
    private String code;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("job注册表中的job有:{}", jobRegistry.getJobNames());
        log.info("job浏览器中的job有:{}", jobExplorer.getJobNames());
        // batchController.test();

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
                    log.info("定时任务已初始化,无需初始化...");
                    taskConfigRepo.save(
                            taskConfigRepo.findByTaskName(e.getTaskName())
                                    .setIsEnabled(true)
                                    .setIsRunning(false)
                                    .setLastModifiedDate(LocalDateTime.now())
                    );
                }, () -> {
                    log.info("定时任务初始化...");
                    TaskConfig taskConfig = new TaskConfig();
                    taskConfig.setTaskName(new YxmsTask().getTaskName());
                    taskConfig.setDescription("易鑫民生跑批任务");
                    taskConfig.setCron("0 0/1 * * * ?");
                    taskConfig.setIsEnabled(true);
                    taskConfig.setIsRunning(false);
                    taskConfig.setCreatedDate(LocalDateTime.now());
                    taskConfigRepo.save(taskConfig);
                }
        );

        //初始化定时任务
        taskConfigRepo.findById(new HtmsTask().getTaskName()).ifPresentOrElse(e -> {
                    log.info("定时任务已初始化,无需初始化...");
                    taskConfigRepo.save(
                            taskConfigRepo.findByTaskName(e.getTaskName())
                                    .setIsEnabled(true)
                                    .setIsRunning(false)
                                    .setLastModifiedDate(LocalDateTime.now())
                    );
                }, () -> {
                    log.info("定时任务初始化...");
                    TaskConfig taskConfig = new TaskConfig();
                    taskConfig.setTaskName(new HtmsTask().getTaskName());
                    taskConfig.setDescription("汇通民生跑批任务");
                    taskConfig.setCron("0 0/1 * * * ?");
                    taskConfig.setIsEnabled(true);
                    taskConfig.setIsRunning(false);
                    taskConfig.setCreatedDate(LocalDateTime.now());
                    taskConfigRepo.save(taskConfig);
                }
        );
    }


    @GetMapping("/initTask")
    public List<TaskConfig> initTask() {
        log.info("开始重新初始化定时任务配置...");
        List<TaskConfig> taskConfigList = taskListScheduler.initTasks();
        log.info("重新初始化定时任务配置结束...");
        return taskConfigList;
    }

    @GetMapping("/cancelTask/{taskName}")
    public TaskConfig cancelTask(@PathVariable String taskName) {
        log.info("开始取消定时任务:{}", taskName);
        return taskListScheduler.cancelTask(taskName);
    }

    @GetMapping("/schedulerInfo")
    public Map<String, Object> schedulerInfo() {
        return taskListScheduler.schedulerInfo();
    }
}