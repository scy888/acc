package com.weshare.batch.task.instance;

import com.weshare.batch.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task.instance
 * @date: 2021-07-05 20:52:27
 * @describe:
 */

@Slf4j
@Component
public class HtmsTask extends BaseTask {
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
