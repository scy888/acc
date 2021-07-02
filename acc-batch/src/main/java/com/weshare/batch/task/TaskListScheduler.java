package com.weshare.batch.task;

import com.weshare.batch.task.entity.TaskConfig;
import com.weshare.batch.task.instance.YxmsTask;
import com.weshare.batch.task.repo.TaskConfigRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

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
    @Autowired
    private TaskConfigRepo taskConfigRepo;
    private static Map<String, ScheduledFuture<?>> map = new HashMap<>();

    private static String machine_address = "unknown address";
    private static String machine_name = "unknown name";

    static {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            machine_address = address.getHostAddress();
            machine_name = address.getHostName();
            log.info("开始打印服务器地址信息,machine_address:{};machine_name:{}", machine_address, machine_name);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0/2 * * * ?")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<TaskConfig> initTasks() {
        map.forEach((k, v) -> {
            log.info("开始取消任务:{}", k);
            v.cancel(true);
        });
        List<TaskConfig> taskConfigList = taskConfigRepo.findAll();
        List<TaskConfig> list = taskConfigList.stream().map(e -> {
            e.setIsEnabled(true)
                    .setIsRunning(false)
                    .setLastModifiedDate(LocalDateTime.now())
                    .setLastModifiedBy(machine_address);
            taskConfigRepo.save(e);
            log.info("初始化任务开始:{}", e.getTaskName());
            map.put(e.getTaskName(), threadPoolTaskScheduler.schedule((Runnable) applicationContext.getBean(e.getTaskName()), new CronTrigger(e.getCron())));
            return e;
        }).collect(Collectors.toList());
        log.info("定时刷新配置表...");
        return list;
    }

    public TaskConfig cancelTask(String taskName) {
        TaskConfig taskConfig = null;
        if (map.containsKey(taskName)) {
            boolean cancel = map.get(taskName).cancel(true);
            log.info("取消任务:{},结果:{}", taskName, cancel);
            taskConfig = taskConfigRepo.save(
                    taskConfigRepo.findByTaskName(taskName)
                            .setIsEnabled(false)
                            .setIsRunning(false)
                            .setLastModifiedDate(LocalDateTime.now())
                            .setLastModifiedBy(machine_address)
            );
        }
        return taskConfig;
    }

    public Map<String, Object> schedulerInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("activeCount", threadPoolTaskScheduler.getActiveCount());
        map.put("taskCount", threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getTaskCount());
        map.put("activeTaskCount", threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getActiveCount());
        map.put("corePoolSize", threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getCorePoolSize());
        map.put("completedTaskCount", threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getCompletedTaskCount());
        map.put("queue", threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getQueue());
        map.put("poolSize", threadPoolTaskScheduler.getPoolSize());
        return map;
    }
}
