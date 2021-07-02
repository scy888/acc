package com.weshare.batch.task;

import com.weshare.batch.task.entity.TaskConfig;
import com.weshare.batch.task.entity.TaskRunLog;
import com.weshare.batch.task.repo.TaskConfigDao;
import com.weshare.batch.task.repo.TaskConfigRepo;
import com.weshare.batch.task.repo.TaskRunLogRepo;
import common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task
 * @date: 2021-06-10 18:40:57
 * @describe:
 */
@Slf4j
public abstract class BaseTask implements Runnable {
    @Autowired
    private TaskConfigDao taskConfigDao;
    @Autowired
    private TaskConfigRepo taskConfigRepo;
    @Autowired
    private TaskRunLogRepo taskRunLogRepo;
    public int num = 6;

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

    public String getTaskName() {
        String simpleName = this.getClass().getSimpleName();
        simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        log.info(String.format("获得当前继承类的名称:%s", simpleName));
        return simpleName;
    }


    public abstract void execte();

    @Override
    public void run() {
        Integer taskRunId = this.before();
        if (taskRunId != null) {
            this.execte();
            this.after(taskRunId);
        }
    }

    public Integer before() {
        Boolean flag = taskConfigDao.lockTask(getTaskName());
        //Boolean isEnabled = taskConfigRepo.findByTaskName(getTaskName()).getIsEnabled();
        if (flag) {
            log.info("任务名:{},被机器名:{},抢占成功", getTaskName(), machine_name);
            TaskRunLog taskRunLog = new TaskRunLog();
            taskRunLog.setTaskName(getTaskName());
            taskRunLog.setTaskParams(JsonUtil.toJson(getTaskParams(), true));
            taskRunLog.setStartTime(LocalDateTime.now());
            taskRunLog.setMachine(machine_address);
            taskRunLog.setRemark(machine_name);
            taskRunLog.setThreadName(Thread.currentThread().getName());
            return taskRunLogRepo.save(taskRunLog).getId();
        } else {
            log.info("任务名:{},正在被其他主机执行,本机不执行...", getTaskName());
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void after(Integer taskRunId) {
        log.info("任务结束,将任务的执行标志重置为false");
        TaskConfig taskConfig = taskConfigRepo.findByTaskName(getTaskName());
        taskConfig.setIsRunning(false);
        taskConfig.setLastModifiedDate(LocalDateTime.now());
        taskConfig.setLastModifiedBy(machine_address);
        taskConfigRepo.save(taskConfig);

        taskRunLogRepo.findById(taskRunId).ifPresent(e -> {
            e.setIsSuccess(true);
            e.setEndTime(LocalDateTime.now());
            e.setTotalTimeCost(
                    new BigDecimal(
                            Duration.between(e.getStartTime(), e.getEndTime()).toMillis()
                    ).setScale(3, RoundingMode.HALF_UP)
            );
            taskRunLogRepo.save(e);
        });
    }

    public abstract Map<String, Object> getTaskParams();
}
