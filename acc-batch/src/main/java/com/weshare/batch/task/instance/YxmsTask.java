package com.weshare.batch.task.instance;

import com.weshare.batch.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task.instance
 * @date: 2021-06-10 18:49:31
 * @describe:
 */

@Slf4j
@Component
public class YxmsTask extends BaseTask {

    public int num = 8;

    public int getNum() {
        return super.num;
    }



    @Override
    public void execte() {
        System.out.println("当前时间：" + LocalDateTime.now() + "执行了...");
    }

    @Override
    public Map<String, Object> getTaskParams() {
        return null;
    }

}
