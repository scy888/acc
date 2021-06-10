package com.weshare.batch.task.instance;

import com.weshare.batch.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    }

    @Override
    public Map<String, Object> getTaskParams() {
        return null;
    }

}
