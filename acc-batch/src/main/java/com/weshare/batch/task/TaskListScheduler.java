package com.weshare.batch.task;

import com.weshare.batch.task.instance.YxmsTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task
 * @date: 2021-06-11 19:30:43
 * @describe:
 */
@Component
@Slf4j
public class TaskListScheduler {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private YxmsTask yxmsTask;

    public void initTask() {
        threadPoolTaskScheduler.schedule((Runnable) applicationContext.getBean(yxmsTask.getTaskName()), new CronTrigger("0/30 * * * * ?"));
    }
}
