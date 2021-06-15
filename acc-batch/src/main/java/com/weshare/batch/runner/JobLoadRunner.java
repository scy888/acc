package com.weshare.batch.runner;

import com.weshare.batch.controller.BatchController;
import com.weshare.batch.task.TaskListScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("job注册表中的job有:{}", jobRegistry.getJobNames());
        log.info("job浏览器中的job有:{}", jobExplorer.getJobNames());
        //batchController.test();
        //taskListScheduler.initTask();
    }
}
