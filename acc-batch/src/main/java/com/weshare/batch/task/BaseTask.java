package com.weshare.batch.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task
 * @date: 2021-06-10 18:40:57
 * @describe:
 */
@Slf4j
@Component
public abstract class BaseTask implements Runnable {

    public int num = 6;

    public String getTaskName() {
        String simpleName = this.getClass().getSimpleName();
        simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        log.info(String.format("获得当前继承类的名称:%s", simpleName));
        return simpleName;
    }

    public abstract void execte();

    @Override
    public void run() {
        execte();
    }

    public abstract Map<String, Object> getTaskParams();
}
