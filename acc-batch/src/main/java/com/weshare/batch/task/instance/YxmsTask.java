package com.weshare.batch.task.instance;

import com.weshare.batch.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
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
        Map<String, Object> params = this.getTaskParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getTaskParams() {

        return Map.ofEntries(Map.entry("执行的当前时间", LocalDateTime.now().withNano(0)));
    }

}
