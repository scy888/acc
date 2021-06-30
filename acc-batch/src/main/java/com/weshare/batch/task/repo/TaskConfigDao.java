package com.weshare.batch.task.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.task.repo
 * @date: 2021-06-30 14:08:49
 * @describe:
 */
@Repository
@Slf4j
public class TaskConfigDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    /**
     * 锁定任务,一个任务,有多个服务实例时,确保只能被一个实例抢到
     * @param taskName 任务名
     * @return 抢占结果 true 抢占成功，false 抢占失败
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.SERIALIZABLE)
    public Boolean lockTask(String taskName) {
        String updateSql = "update task_config set is_running=1,last_modified_by='" + machine_address + "' where task_name=? and is_running=0 and is_enabled=1";
        int rewsult = jdbcTemplate.update(updateSql, taskName);
        if (rewsult > 0) {
            return true;
        }
        return false;
    }
}
